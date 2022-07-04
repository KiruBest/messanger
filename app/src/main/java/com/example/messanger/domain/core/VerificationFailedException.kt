package com.example.messanger.domain.core

import java.lang.Exception

class VerificationFailedException(override val message: String?): Exception()