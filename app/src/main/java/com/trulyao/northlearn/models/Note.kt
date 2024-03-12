package com.trulyao.northlearn.models

import android.content.Context
import java.io.IOException
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.walk

data class Note(val name: String, val size: Int)

data class Content(val name: String, val path: Path, val isDirectory: Boolean)

class NoteService(context: Context) {
    private val notesDir = Path(context.filesDir.toString(), "notes")

    init {
        if (!notesDirExists()) {
            createNotesDir()
        }
    }

    // Get all files in the given directory, the absence of a folder name (null; default) returns the files in the root `notes` directory
    @OptIn(ExperimentalPathApi::class)
    public fun listAllFiles(folderName: String? = null): List<Content> {
        val contents = mutableListOf<Content>()

        val folder = if (folderName == null) {
            notesDir
        } else {
            Path(notesDir.toString(), folderName)
        }

        val folderIter = folder.walk()
        for (file in folderIter) {
            val content = Content(
                name = file.fileName.toString(),
                path = file.toAbsolutePath(),
                isDirectory = file.isDirectory()
            )

            contents.add(content)
        }

        return contents
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