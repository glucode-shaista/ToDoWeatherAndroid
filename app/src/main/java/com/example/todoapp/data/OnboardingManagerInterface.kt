package com.example.todoapp.data

interface OnboardingManagerInterface {
    /**
     * Checks if the user has completed the onboarding flow
     * @return true if onboarding is completed, false otherwise
     */
    fun isOnboardingCompleted(): Boolean
    
    /**
     * Marks the onboarding as completed
     */
    fun setOnboardingCompleted()
    
    /**
     * Resets the onboarding status (useful for testing or re-showing onboarding)
     */
    fun resetOnboarding()
}
