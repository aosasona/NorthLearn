package com.trulyao.northlearn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.trulyao.northlearn.views.Root

class MainActivity : ComponentActivity(), ImageLoaderFactory {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Root()
        }
    }

    // Implement Coil's ImageLoaderFactory to configure image caching
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(applicationContext)
            .memoryCache {
                MemoryCache.Builder(applicationContext)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(applicationContext.cacheDir.resolve("quiz_image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024) // 5MB
                    .build()
            }
            .build()
    }
}
