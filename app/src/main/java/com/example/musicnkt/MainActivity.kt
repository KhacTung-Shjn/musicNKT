package com.example.musicnkt

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener, OnClickItemMusic {

    private var mMusicService: MusicService? = null
    private var mServiceConnection: ServiceConnection? = null
    private var mBound = false
    private var isPlaying = false
    private val listSong = mutableListOf<Song>()
    private var indexSong = 0
    private lateinit var mBroadcast: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initButton()
        fakeListSong()
        bindToService()
        receiverNextSong()
    }

    private fun receiverNextSong() {
        val intentFiler = IntentFilter()
        intentFiler.addAction("NEXT_SONG")
        mBroadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null || intent.action == null) return

                val index = intent.getIntExtra("INDEX_SONG", 0)
                nextSong(index)
            }

        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcast, intentFiler)
    }

    private fun initButton() {
        image_next.setOnClickListener(this)
        image_prev.setOnClickListener(this)
        image_play.setOnClickListener(this)
    }

    private fun fakeListSong() {
        listSong.add(Song("Anh La Cua Em", R.raw.anhlacuaem))
        listSong.add(Song("Dieu Toa", R.raw.dieutoa))
        listSong.add(Song("Hoa May", R.raw.hoamay))
        listSong.add(Song("Hoa No Khong Mau", R.raw.hoanokhongmau))
        listSong.add(Song("Tung La Tat Ca", R.raw.tunglatatca))

        setDetailSong(listSong[indexSong], indexSong)

        val adapter = SongAdapter(listSong)
        adapter.setOnClickItemMusic(this)
        rcvListSong.setHasFixedSize(true)
        rcvListSong.adapter = adapter

    }

    private fun bindToService() {
        mServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val musicBinder: MusicService.MusicBinder = service as MusicService.MusicBinder
                mMusicService = musicBinder.getService()
                mMusicService?.setListSong(listSong)
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName?) {
                mBound = false
            }
        }

        val intent = Intent(this, MusicService::class.java)
        bindService(intent, mServiceConnection as ServiceConnection, BIND_AUTO_CREATE)
    }


    private fun previousSong() {
        if (!mBound) return
        stopSong()
        if (indexSong > 0) {
            indexSong--
        } else {
            indexSong = listSong.size - 1
        }
        setDetailSong(listSong[indexSong], indexSong)
        if (isPlaying) {
            mMusicService?.nextOrPrevSong(indexSong)
        }
    }

    private fun nextSong(index: Int) {
        if (!mBound) return
        this.indexSong = index
        stopSong()
        if (indexSong < listSong.size - 1) {
            indexSong++
        } else {
            indexSong = 0
        }
        setDetailSong(listSong[indexSong], indexSong)
        if (isPlaying) {
            mMusicService?.nextOrPrevSong(indexSong)
        }
    }

    private fun playOrPauseSong(indexSong: Int) {
        if (!mBound) return
        if (isPlaying) {
            image_play.setImageResource(R.drawable.ic_pause)
        } else {
            image_play.setImageResource(R.drawable.ic_play)
        }
        mMusicService?.playOrPauseSong(indexSong)
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.image_play -> {
                isPlaying = !isPlaying
                playOrPauseSong(this.indexSong)
            }
            R.id.image_next -> {
                nextSong(this.indexSong)
            }
            R.id.image_prev -> {
                previousSong()
            }
        }
    }

    override fun onClickItem(song: Song) {
        setDetailSong(song, listSong.indexOf(song))
        this.indexSong = listSong.indexOf(song)
        if (!isPlaying) isPlaying = true
        stopSong()
        playOrPauseSong(indexSong)
    }

    private fun stopSong() {
        if (!mBound) return
        mMusicService?.stopSong()
    }

    private fun setDetailSong(song: Song, indexSong: Int) {
        text_song.text = song.nameSong
        when (indexSong) {
            0 -> {
                image_song.setImageResource(R.drawable.a)
            }
            1 -> {
                image_song.setImageResource(R.drawable.b)
            }
            2 -> {
                image_song.setImageResource(R.drawable.c)
            }
            3 -> {
                image_song.setImageResource(R.drawable.d)
            }
            4 -> {
                image_song.setImageResource(R.drawable.e)
            }
        }
    }

    override fun onDestroy() {
        unbindService(mServiceConnection!!)
        mBound = false
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcast)
        super.onDestroy()
    }

}
