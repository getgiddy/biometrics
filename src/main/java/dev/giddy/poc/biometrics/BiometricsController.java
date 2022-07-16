package dev.giddy.poc.biometrics;

import java.util.Base64;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* Controller for fingerprint scan processing
*/
@RestController
public class BiometricsController {

    @GetMapping("/fingerprint")
    public String enrollFingerFromScanner() {
        final EnrollFingerFromScannerBiometric enrollFromScanner;
        try {
            enrollFromScanner = new EnrollFingerFromScannerBiometric();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "failed 1";
        }

        final ScanThread scanThread = new ScanThread(enrollFromScanner);
        try {
            final FingerPrintDetails fingerPrintDetails = scanFingerPrint(scanThread);
            System.out.print(fingerPrintDetails.getImageBytes());
            System.out.print(fingerPrintDetails.getTemplateBytes());
            return "success";
        } catch (BScannerException e) {
            System.out.print(e.getMessage());
            System.out.print(e.getCode());
            return "failed 2";
        } catch (TimeoutException e) {
            scanThread.stopScan();
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return "failed 3";
        } finally {
            scanThread.stopThread();
        }
        return "failed 4";
    }

    private static FingerPrintDetails scanFingerPrint(
            ScanThread scanThread) throws Exception {

        Future<FingerPrintDetails> future = scanThread.start();

        FingerPrintDetails fingerPrintDetails = future.get(3000, TimeUnit.MILLISECONDS);
        // if internal exception
        if (fingerPrintDetails == null) {
            throw scanThread.getException();
        }

        byte[] imageBytes = fingerPrintDetails.getImageBytes();
        byte[] templateBytes = fingerPrintDetails.getTemplateBytes();

        byte[] imageEncoded = Base64
                .getEncoder()
                .encode(imageBytes);
        byte[] templateEncoded = Base64
                .getEncoder()
                .encode(templateBytes);

        return new FingerPrintDetails(imageEncoded, templateEncoded);
    }
}
