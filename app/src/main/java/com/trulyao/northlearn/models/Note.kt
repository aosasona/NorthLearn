package com.trulyao.northlearn.models

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.createFile
import kotlin.io.path.deleteIfExists
import kotlin.io.path.deleteRecursively
import kotlin.io.path.exists
import kotlin.io.path.extension
import kotlin.io.path.fileSize
import kotlin.io.path.isDirectory
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.readText
import kotlin.io.path.writeText

class AppException : Exception {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Throwable) : super(message, cause)
    constructor(cause: Throwable) : super(cause)
}

data class Content(
    val name: String,
    val extension: String,
    val path: Path,
    val absolutePath: Path,
    val size: Long,
    val isDirectory: Boolean,
)

data class NoteModel(
    val name: String,
    var content: MutableState<String>,
    val path: Path,
)

class NoteService(context: Context) {
    private val notesDir = Path(context.filesDir.toString(), "notes")

    init {
        if (!notesDirExists()) {
            createNotesDir()
        }
    }

    public fun pathToNavFriendlyString(path: Path): String {
        return path.toString()
            .replace(notesDir.toString(), "")
            .trim { it == '/' }
            .replace("/", ".")
    }

    // Get all files in the given directory, the absence of a folder name (null; default) returns the files in the root `notes` directory
    @OptIn(ExperimentalPathApi::class)
    public suspend fun listAllFiles(folderName: String? = null): List<Content> {
        val contents = arrayListOf<Content>()

        val folder = if (folderName == null) {
            notesDir
        } else {
            Path(notesDir.toString()).resolve(folderName)
        }

        withContext(Dispatchers.IO) {
            Files.walk(folder, 1).filter { it != folder }
        }.forEach { file ->
            val content = Content(
                name = file.nameWithoutExtension,
                extension = file.extension,
                path = file,
                absolutePath = file.toAbsolutePath(),
                size = file.fileSize(),
                isDirectory = file.isDirectory()
            )

            contents.add(content)
        }

        return contents.sortedBy { content -> !content.isDirectory }
    }

    public suspend fun getNote(filename: String): NoteModel {
        val path = Path(notesDir.toString(), filename)
        if (!path.exists()) throw Exception("File not found")

        return NoteModel(
            name = path.nameWithoutExtension,
            path = path,
            content = mutableStateOf(path.readText(Charset.defaultCharset()))
        )
    }

    public suspend fun saveNote(path: Path, content: String) {
        if (!path.exists()) throw Exception("File not found")
        path.writeText(content, Charset.defaultCharset())
    }

    public suspend fun createDirectory(path: Path): Content {
        val folderPath = Path(notesDir.toString(), path.toString());
        if (folderPath.exists()) {
            throw AppException("Folder already exists")
        }

        val target = folderPath.createDirectory()
        return Content(
            name = target.fileName.toString(),
            extension = target.extension,
            path = target,
            absolutePath = target.toAbsolutePath(),
            size = target.fileSize(),
            isDirectory = true
        )
    }

    public suspend fun createFile(path: Path): Content {
        val filePath = Path(notesDir.toString(), path.toString());
        if (filePath.exists()) {
            throw AppException("File already exists")
        }

        val target = filePath.createFile()
        return Content(
            name = target.fileName.toString(),
            extension = target.extension,
            path = target,
            absolutePath = target.toAbsolutePath(),
            size = target.fileSize(),
            isDirectory = true
        )
    }

    @OptIn(ExperimentalPathApi::class)
    public suspend fun deleteItem(path: Path) {
        if (path.isDirectory()) {
            path.deleteRecursively()
            return;
        }

        path.deleteIfExists()
    }

    private fun notesDirExists(): Boolean {
        return notesDir.exists();
    }

    private fun createNotesDir(): Path? {
        return try {
            notesDir.createDirectory()
        } catch (e: IOException) {
            System.err.println(e.message)
            null
        }
    }
}

// Convert raw byte sizes to an human-readable format (B, KB, MB, GB)
public fun bytesToHumanReadableFormat(originalSize: Long): String {
    val size = originalSize.toDouble()
    val humanReadableSize: Double
    val measurement: String

    if (size < 1000) {
        humanReadableSize = size
        measurement = "B"
    } else if (size < 1_000_000) {
        humanReadableSize = size / 1024
        measurement = "KB"
    } else if (size < 1_000_000_000) {
        humanReadableSize = size / (1024 * 1024)
        measurement = "MB"
    } else {
        humanReadableSize = size / (1024 * 1024 * 1024)
        measurement = "GB"
    }

    return "%.2f%s".format(humanReadableSize, measurement)
}
