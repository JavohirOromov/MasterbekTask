package com.javohir.masterbektask.di
import android.content.Context
import com.javohir.masterbektask.data.local.VideoAssetProvider
import com.javohir.masterbektask.data.mapper.VideoTypeMapper
import com.javohir.masterbektask.data.repository.ConversationRepositoryImpl
import com.javohir.masterbektask.domain.repository.ConversationRepository
import com.javohir.masterbektask.domain.useCase.DetectKeywordUseCase
import com.javohir.masterbektask.domain.useCase.GetVideoForKeywordUseCase
import com.javohir.masterbektask.utils.SpeechRecognizerHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by: Javohir Oromov macos
 * Project: MasterbekTask
 * Package: com.javohir.masterbektask.di
 * Description: Dependency Injection Module
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideVideoAssetProvider(
        @ApplicationContext context: Context
    ): VideoAssetProvider {
        return VideoAssetProvider(context)
    }

    @Provides
    @Singleton
    fun provideVideoTypeMapper(): VideoTypeMapper {
        return VideoTypeMapper
    }

    @Provides
    @Singleton
    fun provideConversationRepository(
        videoAssetProvider: VideoAssetProvider,
        videoTypeMapper: VideoTypeMapper
    ): ConversationRepository {
        return ConversationRepositoryImpl(videoAssetProvider, videoTypeMapper)
    }


    @Provides
    @Singleton
    fun provideDetectKeywordUseCase(): DetectKeywordUseCase {
        return DetectKeywordUseCase()
    }


    @Provides
    @Singleton
    fun provideGetVideoForKeywordUseCase(
        repository: ConversationRepository,
        detectKeywordUseCase: DetectKeywordUseCase
    ): GetVideoForKeywordUseCase {
        return GetVideoForKeywordUseCase(repository, detectKeywordUseCase)
    }

    @Provides
    @Singleton
    fun provideSpeechRecognizerHelper(
        @ApplicationContext context: Context
    ): SpeechRecognizerHelper {
        return SpeechRecognizerHelper(context)
    }
}