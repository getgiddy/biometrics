package dev.giddy.poc.biometrics;

import lombok.*;

@Value
class FingerPrintDetails {
    byte[] imageBytes;
    byte[] templateBytes;
}
