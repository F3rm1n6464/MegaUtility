package com.example.xupermega.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.xupermega.ui.main.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel
}

@kotlin.annotation.Retention(kotlin.annotation.RetentionPolicy.RUNTIME)
@kotlin.annotation.Target(
    kotlin.annotation.AnnotationTarget.FUNCTION,
    kotlin.annotation.AnnotationTarget.PROPERTY_GETTER,
    kotlin.annotation.AnnotationTarget.PROPERTY_SETTER
)
@dagger.MapKey
annotation class ViewModelKey(val value: kotlin.reflect.KClass<out ViewModel>)