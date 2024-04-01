package com.informatika.bondoman.view.fragment.transaction

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.informatika.bondoman.BuildConfig
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.ListTransactionFragmentBinding
import com.informatika.bondoman.model.Resource
import com.informatika.bondoman.util.CameraUtil
import com.informatika.bondoman.view.activity.MainActivity.Companion.createTransactionFragmentTag
import com.informatika.bondoman.view.activity.ScanPreviewActivity
import com.informatika.bondoman.view.adapter.TransactionRecyclerAdapter
import com.informatika.bondoman.viewmodel.transaction.ListTransactionViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File


class ListTransactionFragment : Fragment() {

    private lateinit var mListTransactionFragmentBinding: ListTransactionFragmentBinding
    private val listTransactionViewModel: ListTransactionViewModel by viewModel()
    private lateinit var transactionRecyclerAdapter: TransactionRecyclerAdapter
    private lateinit var cameraLauncher: ActivityResultLauncher<String>
    private lateinit var imageResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mListTransactionFragmentBinding =
            ListTransactionFragmentBinding.inflate(inflater, container, false)
        return mListTransactionFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListTransactionFragmentBinding.fabAddTransaction.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_activity_container, CreateTransactionFragment.newInstance())
                .addToBackStack(createTransactionFragmentTag)
                .commit()
        }

        mListTransactionFragmentBinding.fabScanTransaction.setOnClickListener {
            startScanDialog()
        }

        initViews()
        listenToViewModel()
        fetchTransactions()
    }

    override fun onResume() {
        super.onResume()
        fetchTransactions()
    }

    private fun fetchTransactions() {
        listTransactionViewModel.getAllTransaction()
    }

    private fun listenToViewModel() {
        listTransactionViewModel.listTransactionLiveData.observe(
            viewLifecycleOwner
        ) { resource ->
            resource?.let {
                when (it) {
                    is Resource.Success -> {
                        transactionRecyclerAdapter.removeLoader()
                        transactionRecyclerAdapter.setTransactionList(ArrayList(it.data))
                    }

                    is Resource.Error ->
                        transactionRecyclerAdapter.removeLoader()

                    is Resource.Loading ->
                        transactionRecyclerAdapter.addLoader()
                }
                displayTransactions()
            }
        }
    }

    private fun initViews() {
        transactionRecyclerAdapter = TransactionRecyclerAdapter(requireContext())
        mListTransactionFragmentBinding.rvTransactions.adapter = transactionRecyclerAdapter

        val animator = mListTransactionFragmentBinding.rvTransactions.itemAnimator
        if (animator is androidx.recyclerview.widget.SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        if (!transactionRecyclerAdapter.isLoading()) {
            transactionRecyclerAdapter.addLoader()
        }

        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    openChooser()
                }
            }

        imageResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageResult = getPickImageResultUri(result.data)

                if (imageResult != null) {
                    val intent = Intent(requireContext(), ScanPreviewActivity::class.java)

                    intent.putExtra("image", imageResult)
                    startActivity(intent)
                }

                Timber.tag("Scanner").d(imageResult.toString())
            }
        }
    }

    private fun displayTransactions() {
        if (transactionRecyclerAdapter.itemCount == 0) {
            mListTransactionFragmentBinding.rvTransactions.visibility = View.GONE
            mListTransactionFragmentBinding.tvEmpty.visibility = View.VISIBLE
        } else {
            mListTransactionFragmentBinding.rvTransactions.visibility = View.VISIBLE
            mListTransactionFragmentBinding.tvEmpty.visibility = View.GONE
        }
    }

    private fun startScanDialog() {
        if (!CameraUtil.checkCameraPermission(requireContext())) {
            cameraLauncher.launch(Manifest.permission.CAMERA)
        } else {
            openChooser()
        }
    }

    private fun openChooser() {
        imageResultLauncher.launch(getPickImageChooserIntent())
    }

    private fun getCaptureImageOutputUri(): Uri {
        val getImage = requireContext().cacheDir

        val outputFile = File(getImage.path, "bondoman_transaction_image.png")

        return FileProvider.getUriForFile(
            requireActivity(),
            BuildConfig.APPLICATION_ID + ".provider",
            outputFile
        )
    }

    private fun getPickImageChooserIntent(): Intent {
        val outputUri = getCaptureImageOutputUri()

        val intents = arrayListOf<Intent>()

        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // collect all camera intents
        val listCam = requireActivity().packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            intents.add(intent)
        }

        // collect all gallery intents
        val galleryIntent = Intent(Intent.ACTION_GET_CONTENT)
        galleryIntent.type = "image/*"
        val listGallery = requireActivity().packageManager.queryIntentActivities(galleryIntent, 0)
        for (res in listGallery) {
            val intent = Intent(captureIntent)
            intent.component = ComponentName(res.activityInfo.packageName, res.activityInfo.name)
            intent.setPackage(res.activityInfo.packageName)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            intents.add(intent)
        }

        // the main intent is the last in the list (fucking android) so pickup the useless one
        var mainIntent: Intent? = intents[intents.size - 1]
        for (intent in intents) {
            if (intent.component!!.className == "com.android.documentsui.DocumentsActivity") {
                mainIntent = intent
                break
            }
        }
        intents.remove(mainIntent)

        // Create a chooser from the main intent
        val chooserIntent = Intent.createChooser(mainIntent, "Select source")

        // Add all other intents
        chooserIntent.putExtra(
            Intent.EXTRA_INITIAL_INTENTS,
            intents.toArray(arrayOfNulls<Parcelable>(intents.size))
        )

        return chooserIntent
    }

    private fun getPickImageResultUri(data: Intent?): Uri? {
        var isCamera = true
        if (data != null) {
            val action = data.action
            isCamera = action != null && action == MediaStore.ACTION_IMAGE_CAPTURE
        }
        return if (isCamera) getCaptureImageOutputUri() else data!!.data
    }

    companion object {
        fun newInstance() = ListTransactionFragment()
    }
}
