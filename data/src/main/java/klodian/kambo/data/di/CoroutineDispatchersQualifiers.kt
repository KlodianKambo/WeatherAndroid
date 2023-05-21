package klodian.kambo.data.di

/**
 * Created by Federico Monti on 02/02/23.
 */
import javax.inject.Qualifier

@Qualifier
annotation class DefaultDispatcher

@Qualifier
annotation class IoDispatcher

@Qualifier
annotation class MainDispatcher

@Qualifier
annotation class MainImmediateDispatcher