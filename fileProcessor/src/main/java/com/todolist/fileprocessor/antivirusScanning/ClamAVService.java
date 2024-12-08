package com.todolist.fileprocessor.antivirusScanning;

import com.todolist.fileprocessor.config.ClamAVProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

@Service
@AllArgsConstructor
public class ClamAVService {
    private final ClamAVProperties clamAVProperties;

    public String scanFile(File file) throws IOException {
        try (Socket socket = new Socket(clamAVProperties.getUrl(), clamAVProperties.getPort())) {
            socket.getOutputStream().write(("SCAN " + file.getAbsolutePath() + "\n").getBytes());
            byte[] response = socket.getInputStream().readAllBytes();
            return new String(response);
        }
    }
}