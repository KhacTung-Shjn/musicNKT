package com.example.musicnkt

import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager

class MusicService : Service(), MediaPlayer.OnCompletionListener {

    private lateinit var iBinder: IBinder
    private var listSong = mutableListOf<Song>()
    private var mediaPlayer: MediaPlayer? = null
    private var mState: Int = 0
    private var mCurrentIndex: Int = 0

    private fun setMediaPlayer(mediaPlayer: MediaPlayer) {
        this.mediaPlayer = mediaPlayer
    }

    override fun onCreate() {
        super.onCreate()
        iBinder = MusicBinder()
    }

    fun setListSong(listSong: MutableList<Song>) {
        this.listSong = listSong
    }

    override fun onBind(p0: Intent?): IBinder? {
        return iBinder
    }

    fun playOrPauseSong(indexSong: Int) {
        this.mCurrentIndex = indexSong
        if (mState == StateSong.IDLE || mState == StateSong.STOPPED) {
            val song: Song = listSong[mCurrentIndex]
            val media: MediaPlayer = MediaPlayer.create(this, song.idSong)
            setMediaPlayer(media)
            mediaPlayer!!.start()
            mediaPlayer!!.setOnCompletionListener(this)
            mState = StateSong.PLAYING
            return
        }

        if (mState == StateSong.PLAYING) {
            mState = StateSong.PAUSED
            mediaPlayer!!.pause()
            return
        }

        mediaPlayer!!.start()
        mState = StateSong.PLAYING
    }

    fun stopSong() {
        mediaPlayer?.let {
            mediaPlayer!!.stop()
            mState = StateSong.STOPPED
        }
    }

    fun nextOrPrevSong(indexSong: Int) {
        this.mCurrentIndex = indexSong
        stopSong()
        playOrPauseSong(mCurrentIndex)
    }

    override fun onCompletion(p0: MediaPlayer?) {
        val intent = Intent()
        intent.action = "NEXT_SONG"
        intent.putExtra("INDEX_SONG",this.mCurrentIndex)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }

    inner class MusicBinder : Binder() {
        fun getService(): MusicService {
            return this@MusicService
        }
    }

    override fun onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer!!.release()
            mState = StateSong.IDLE
        }
        super.onDestroy()
    }
}
