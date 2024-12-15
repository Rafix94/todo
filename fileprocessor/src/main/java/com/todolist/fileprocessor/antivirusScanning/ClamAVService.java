package com.todolist.fileprocessor.antivirusScanning;

import com.todolist.fileprocessor.config.ClamAVProperties;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

@Service
@AllArgsConstructor
public class ClamAVService {
    private final ClamAVProperties clamAVProperties;

    public String scan(InputStream fileInputStream) throws IOException {
        try (Socket socket = new Socket(clamAVProperties.getUrl(), clamAVProperties.getPort());
             OutputStream outputStream = socket.getOutputStream();
             InputStream inputStream = socket.getInputStream()) {

            outputStream.write("zINSTREAM\0".getBytes());
            outputStream.flush();

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(ByteBuffer.allocate(4).putInt(bytesRead).array());
                outputStream.write(buffer, 0, bytesRead);
            }

            outputStream.write(ByteBuffer.allocate(4).putInt(0).array());
            outputStream.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            return reader.readLine();
        } catch (IOException e) {
            throw new IOException("Error connecting to ClamAV: " + e.getMessage(), e);
        }
    }
}