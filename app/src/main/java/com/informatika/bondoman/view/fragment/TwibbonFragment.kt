package com.informatika.bondoman.view.fragment

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.informatika.bondoman.R
import com.informatika.bondoman.databinding.FragmentTwibbonBinding
import com.informatika.bondoman.viewmodel.TwibbonViewModel
import timber.log.Timber

class TwibbonFragment : Fragment() {
    lateinit var mTwibbonFragmentBinding: FragmentTwibbonBinding
    private val viewModel: TwibbonViewModel by viewModels()
    private var imageCapture: ImageCapture? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private var previewTaken: Boolean = false

    companion object {
        fun newInstance() = TwibbonFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mTwibbonFragmentBinding = FragmentTwibbonBinding.inflate(inflater, container, false)

        val cameraPermission = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                openCamera()
            }
        }

        cameraPermission.launch(Manifest.permission.CAMERA)
        imageCapture = ImageCapture.Builder().build()

        mTwibbonFragmentBinding.btnCapture.setOnClickListener {
            takeImage()
        }

        return mTwibbonFragmentBinding.root
    }

    private fun openCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    if (!previewTaken) {
                        it.setSurfaceProvider(mTwibbonFragmentBinding.previewView.surfaceProvider)
                    } else {
                        it.setSurfaceProvider(null)
                    }
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider?.unbindAll()
                cameraProvider?.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview
                )
            } catch (e: Exception) {
                Timber.e(e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takeImage() {
        previewTaken = !previewTaken
        val btnCapture: ImageButton = mTwibbonFragmentBinding.btnCapture
        if (previewTaken) {
            btnCapture.setImageResource(R.drawable.ic_retake)
        } else {
            btnCapture.setImageResource(R.drawable.ic_capture)
        }
        openCamera()
    }
}