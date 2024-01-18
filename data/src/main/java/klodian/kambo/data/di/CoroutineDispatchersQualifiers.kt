package klodian.kambo.data.di


import javax.inject.Qualifier

@Qualifier
annotation class DefaultDispatcher

@Qualifier
annotation class IoDispatcher

@Qualifier
annotation class MainDispatcher

@Qualifier
annotation class MainImmediateDispatcher