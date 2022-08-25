package it.lucapizzini.android_barcode_scan_example.facedetect

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.RectF
import android.widget.Toast
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.example.sampleface.BarAndFaceBoxView
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class FaceDetectAndQRAnalyzer(
    private val context: Context,
    private val barcodeBoxView: BarAndFaceBoxView,
    private val previewViewWidth: Float,
    private val previewViewHeight: Float
) : ImageAnalysis.Analyzer {


    /**
     * This parameters will handle preview box scaling
     */
    private var scaleX = 1f
    private var scaleY = 1f

    private fun translateX(x: Float) = x * scaleX
    private fun translateY(y: Float) = y * scaleY

    private fun adjustBoundingRect(rect: Rect) = RectF(
        translateX(rect.left.toFloat()),
        translateY(rect.top.toFloat()),
        translateX(rect.right.toFloat()),
        translateY(rect.bottom.toFloat())
    )


    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        val img = image.image
        if (img != null) {

            scaleX = previewViewWidth / img.height.toFloat()
            scaleY = previewViewHeight / img.width.toFloat()

            val inputImage = InputImage.fromMediaImage(img, image.imageInfo.rotationDegrees)


            // High-accuracy landmark detection and face classification
            val faceOptions = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()

            val qrOptions = BarcodeScannerOptions.Builder()
                .build()

            //faceDetector
            faceDetector(inputImage, faceOptions)

            //QRCode
            qrCode(inputImage, qrOptions)

        }

        image.close()
    }

    private fun qrCode(inputImage: InputImage, options: BarcodeScannerOptions) {

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isNotEmpty()) {
                    for (barcode in barcodes) {
                        // Handle received barcodes...
                        Toast.makeText(
                            context,
                            "Value: " + barcode.rawValue,
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        // Update bounding rect
                        barcode.boundingBox?.let { rect ->
                            barcodeBoxView.setRect(
                                adjustBoundingRect(
                                    rect
                                )
                            )
                        }
                    }
                } else {
                    // Remove bounding rect
                    barcodeBoxView.setRect(RectF())
                }
            }
            .addOnFailureListener { }

    }

    private fun faceDetector(inputImage: InputImage, options: FaceDetectorOptions) {

        val scanner = FaceDetection.getClient(options)

        scanner.process(inputImage)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    for (face in faces) {
                        val bounds = face.boundingBox
                        val rotY = face.headEulerAngleY // Head is rotated to the right rotY degrees
                        val rotZ = face.headEulerAngleZ // Head is tilted sideways rotZ degrees

                        // Update bounding rect
                        face.boundingBox.let { rect ->
                            barcodeBoxView.setRect(
                                adjustBoundingRect(
                                    rect
                                )
                            )
                        }

                        // Handle received barcodes...
                        Toast.makeText(
                            context,
                            "bounds: ${bounds}\n" +
                                    "rotY: ${rotY}\n" +
                                    "rotZ: ${rotZ}",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    // Remove bounding rect
                    barcodeBoxView.setRect(RectF())
                }
            }
            .addOnFailureListener { }

    }


}


