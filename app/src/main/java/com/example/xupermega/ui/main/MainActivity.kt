package com.example.xupermega.ui.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.xupermega.databinding.ActivityMainBinding
import com.example.xupermega.model.LogEntry
import com.example.xupermega.model.LogLevel
import com.example.xupermega.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var logAdapter: LogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        logAdapter = LogAdapter()
        binding.rvLogs.layoutManager = LinearLayoutManager(this)
        binding.rvLogs.adapter = logAdapter
    }

    private fun setupObservers() {
        viewModel.logs.observe(this, Observer { logs ->
            logAdapter.submitList(logs)
            if (logs.isNotEmpty()) {
                binding.rvLogs.scrollToPosition(logs.size - 1)
            }
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            binding.btnStartDownload.isEnabled = !isLoading
            binding.btnRotateIp.isEnabled = !isLoading && !(viewModel.isRotating.value == true)
            if (isLoading) {
                binding.btnStartDownload.text = "Loading..."
            } else {
                binding.btnStartDownload.text = getString(R.string.btn_start_download)
            }
        })

        viewModel.isRotating.observe(this, Observer { isRotating ->
            binding.btnRotateIp.isEnabled = !isRotating && !(viewModel.isLoading.value == true)
            binding.btnStartDownload.isEnabled = !isRotating && !(viewModel.isLoading.value == true)
            if (isRotating) {
                binding.btnRotateIp.text = "Rotating..."
            } else {
                binding.btnRotateIp.text = getString(R.string.btn_rotate_ip)
            }
        })

        viewModel.isAdLoading.observe(this, Observer { isAdLoading ->
            if (isAdLoading) {
                Toast.makeText(this, R.string.toast_ad_loading, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnStartDownload.setOnClickListener {
            val url = binding.etUrl.text.toString().trim()
            if (url.isNotEmpty()) {
                viewModel.onUrlChanged(url)
                viewModel.onStartDownload()
            } else {
                Toast.makeText(this, R.string.toast_enter_url, Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnRotateIp.setOnClickListener {
            viewModel.onRotateIp()
        }

        binding.etUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                val url = binding.etUrl.text.toString().trim()
                if (url.isNotEmpty()) {
                    viewModel.onUrlChanged(url)
                    viewModel.onStartDownload()
                }
                true
            } else {
                false
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}