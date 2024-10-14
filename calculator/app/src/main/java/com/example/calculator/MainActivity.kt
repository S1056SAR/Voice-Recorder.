package com.example.voicerecorder

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.calculator.R
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recordButton: Button
    private lateinit var stopButton: Button
    private lateinit var playButton: Button
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.recordButton)
        stopButton = findViewById(R.id.stopButton)
        playButton = findViewById(R.id.playButton)

        stopButton.isEnabled = false
        playButton.isEnabled = false

        if (!hasMicrophone()) {
            recordButton.isEnabled = false
        }

        audioFilePath = externalCacheDir?.absolutePath + "/audio_record.3gp"

        recordButton.setOnClickListener {
            if (checkPermission()) {
                startRecording()
                stopButton.isEnabled = true
                playButton.isEnabled = false
                recordButton.isEnabled = false
            }
        }

        stopButton.setOnClickListener {
            stopRecording()
            stopButton.isEnabled = false
            playButton.isEnabled = true
            recordButton.isEnabled = true
        }

        playButton.setOnClickListener {
            startPlaying()
        }
    }

    private fun startRecording() {
        mediaRecorder = MediaRecorder(Context context).apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            setOutputFile(audioFilePath)
            try {
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null
    }

    private fun startPlaying() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun hasMicrophone(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    private fun checkPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return false
        }
        return true
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}