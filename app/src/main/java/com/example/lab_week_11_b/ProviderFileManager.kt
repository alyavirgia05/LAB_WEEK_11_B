package com.example.lab_week_11_b

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import org.apache.commons.io.IOUtils
import java.io.File
import java.util.concurrent.Executor

// Helper class to manage files in MediaStore
class ProviderFileManager(
    private val context: Context,
    private val fileHelper: FileHelper,
    private val contentResolver: ContentResolver,
    private val executor: Executor,
    private val mediaContentHelper: MediaContentHelper
) {
    // Generate the data model (FileInfo) for the file
    // The data model contains the URI, file, name, relative path, and MIME type of the file
    fun generatePhotoUri(time: Long): FileInfo {
        val name = "img_$time.jpg"
        // Get the file object
        // The file will be stored in the folder defined in file_provider_paths.xml
        val file = File(
            context.getExternalFilesDir(fileHelper.getPicturesFolder()),
            name
        )
        return FileInfo(
            fileHelper.getUriFromFile(file),
            file,
            name,
            fileHelper.getPicturesFolder(),
            "image/jpeg"
        )
    }
    fun generateVideoUri(time: Long): FileInfo {
        val name = "video_$time.mp4"
        // Get the file object
        // The file will be stored in the folder defined in file_provider_paths.xml
        val file = File(
            context.getExternalFilesDir(fileHelper.getVideosFolder()),
            name
        )
        return FileInfo(
            fileHelper.getUriFromFile(file),
            file,
            name,
            fileHelper.getVideosFolder(),
            "video/mp4"
        )
    }
    // Insert the image/video to MediaStore
    fun insertImageToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                fileInfo,
                mediaContentHelper.getImageContentUri(),
                mediaContentHelper.generateImageContentValues(it)
            )
        }
    }
    fun insertVideoToStore(fileInfo: FileInfo?) {
        fileInfo?.let {
            insertToStore(
                fileInfo,
                mediaContentHelper.getVideoContentUri(),
                mediaContentHelper.generateVideoContentValues(it)
            )
        }
    }

    // Insert the file to MediaStore
    // The file will be copied to the given relative path
    // Input Stream is used to read the file
    // Output Stream is used to write the file
    private fun insertToStore(fileInfo: FileInfo, contentUri: Uri,
                              contentValues: ContentValues
    ) {
        executor.execute {
            val insertedUri = contentResolver.insert(contentUri,
                contentValues)
            insertedUri?.let {
                val inputStream =
                    contentResolver.openInputStream(fileInfo.uri)
                val outputStream =
                    contentResolver.openOutputStream(insertedUri)
                IOUtils.copy(inputStream, outputStream)
            }
        }
    }
}