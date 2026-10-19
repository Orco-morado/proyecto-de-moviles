package com.example.proyectoappmovil.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class InstantTaskExecutorRule : TestRule {

    private val delegate = InstantTaskExecutorRule()

    override fun apply(base: Statement, description: Description): Statement {
        return delegate.apply(base, description)
    }
}