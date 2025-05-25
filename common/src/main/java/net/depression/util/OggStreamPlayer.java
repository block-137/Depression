package net.depression.util;

import net.depression.Depression;
import net.depression.screen.rhythmcraft.SongProgressSlider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class OggStreamPlayer {
    public SongProgressSlider slider;
    private SourceDataLine sourceLine;
    private AudioInputStream audioInputStream;
    private AudioFormat decodedFormat; // 解码后的格式
    private AudioFormat fileFormat; // 文件格式
    public boolean isPlaying = false;
    public boolean isPaused = false;
    public boolean isEscPaused = false;
    public boolean isSpacePaused = false;
    private Thread playThread;
    private long totalBytesRead = 0;
    private long bytesToSkip = 0;
    private double soughtTime = 0;
    private String filePath;
    private FloatControl volumeControl;
    private float initialVolume;
    private float midVolume;
    private float minVolume;
    private float maxVolume;
    private double durationInSeconds;
    private long bytesToIgnore;
    private boolean wasPaused;
    public boolean isForwarding;
    private Thread forwardThread;
    public void load(String filePath, double durationInSeconds) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
        this.filePath = filePath; // 保存文件路径
        // 获取音频输入流
        AudioInputStream in = AudioSystem.getAudioInputStream(new File(filePath));
        AudioFormat baseFormat = in.getFormat();
        fileFormat = AudioSystem.getAudioFileFormat(new File(filePath)).getFormat();
        this.durationInSeconds = durationInSeconds;
        // 创建解码后的音频格式
        decodedFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                baseFormat.getSampleRate(),
                16, // 16位
                baseFormat.getChannels(),
                baseFormat.getChannels() * 2, // 每帧的字节数 (16位 = 2字节)
                baseFormat.getSampleRate(),
                false // 大端(false)或小端(true)，通常为false（小端）
        );

        // 转换为解码后的音频流
        audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, in);

        // 获取数据行信息
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);
        // 获取数据行
        sourceLine = (SourceDataLine) AudioSystem.getLine(info);
        sourceLine.open(decodedFormat);

        if (sourceLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            volumeControl = (FloatControl) sourceLine.getControl(FloatControl.Type.MASTER_GAIN);
            Options options = Minecraft.getInstance().options;
            initialVolume = options.getSoundSourceVolume(SoundSource.MUSIC);
            midVolume = volumeControl.getValue();
            float gap = Math.min(midVolume - volumeControl.getMinimum(), volumeControl.getMaximum() - midVolume);
            minVolume = midVolume - gap;
            maxVolume = midVolume + gap;
        }
        else {
            Depression.LOGGER.error("Volume control not supported");
        }
    }

    public void play() {
        isPlaying = true;
        isPaused = false;
        sourceLine.start();

        playThread = new Thread(() -> {
            byte[] buffer = new byte[4096];
            int bytesRead = 0;
            totalBytesRead = 0;
            try {
                while (isPlaying && (bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                    while (isPaused) {
                        Thread.sleep(100);
                    }
                    bytesRead -= bytesRead % decodedFormat.getFrameSize();
                    sourceLine.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                sourceLine.drain();
                sourceLine.stop();
                sourceLine.close();
                try {
                    audioInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                isPlaying = false;
                isPaused = false;
            }
        });

        playThread.start();
    }

    public void pause() {
        isPaused = true;
        sourceLine.stop();
    }

    public void resume() {
        if (isEscPaused || isSpacePaused) {
            return;
        }
        isPaused = false;
        sourceLine.start();
    }

    public void stop() {
        isPlaying = false;
        isPaused = false;
        if (playThread != null && playThread.isAlive()) {
            try {
                playThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void seek(long ticks) {
        boolean wasPaused = isPaused;
        pause();
        sourceLine.stop();
        sourceLine.flush();
        sourceLine.close();
        double seconds = ticks / 20d;
        // 计算应跳过的字节数
        int frameSize = decodedFormat.getFrameSize();
        long bytesPerSecond = (long) (frameSize * decodedFormat.getFrameRate());
        long totalBytesToSkip = (long) (seconds * bytesPerSecond);
        totalBytesToSkip -= totalBytesToSkip % frameSize;

        bytesToIgnore = (long) (Math.min(1d, seconds) * bytesPerSecond);
        bytesToSkip = totalBytesToSkip - bytesToIgnore;
        try {
            audioInputStream.close();
            // 重新加载音频文件
            AudioInputStream in = AudioSystem.getAudioInputStream(new File(filePath));
            // 转换为解码后的音频流
            audioInputStream = AudioSystem.getAudioInputStream(decodedFormat, in);
            totalBytesRead = 0;
            while (totalBytesRead < bytesToSkip) {
                long skipped = audioInputStream.skip(bytesToSkip - totalBytesRead);
                if (skipped <= 0) {
                    break;
                }
                totalBytesRead += skipped;
            }

            byte[] ignoreBuffer = new byte[4096];
            long ignored;
            while (totalBytesRead < totalBytesToSkip) {
                try {
                    int length;
                    if (totalBytesToSkip - totalBytesRead < ignoreBuffer.length) {
                        length = (int) (totalBytesToSkip - totalBytesRead);
                    }
                    else {
                        length = ignoreBuffer.length;
                    }
                    ignored = audioInputStream.read(ignoreBuffer, 0, length - length % frameSize);
                    if (length % frameSize > 0 && 0 < ignored && ignored < ignoreBuffer.length) { // 读取不足一帧则不读取，避免死循环
                        totalBytesRead += ignored;
                        break;
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (ignored < 0) {
                    break;
                }
                totalBytesRead += ignored;
            }
            sourceLine.open(decodedFormat);
            soughtTime = totalBytesRead / (decodedFormat.getFrameRate() * decodedFormat.getFrameSize());
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            throw new RuntimeException(e);
        }
        isPaused = wasPaused;
        if (!isPaused) {
            sourceLine.start();
        }
    }
    public void forward(long ticks){
        wasPaused = isPaused;
        isPaused = false;
        sourceLine.start();
        isForwarding = true;
        forwardThread = new Thread(() -> {
            try {
                Thread.sleep(ticks * 50);
                if (wasPaused) {
                    pause();
                }
                isForwarding = false;
                forwardThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        forwardThread.start();
    }
    public double getElapsedTimeInSeconds() {
        return soughtTime + sourceLine.getMicrosecondPosition() / 1000000d;
    }

    public void setVolume(float value) {
        if (volumeControl != null) {
            value = Math.max(volumeControl.getMinimum(), Math.min(volumeControl.getMaximum(), value));
            volumeControl.setValue(value);
        }
    }
    public void setVolumeLinear(float volume) {
        if (volumeControl != null) {
            float dB;
            if (volume == 0) {
                dB = volumeControl.getMinimum();
            }
            else if (volume < 0.5f) {
                dB = (midVolume - minVolume) * volume + minVolume;
            }
            else {
                dB = (maxVolume - midVolume) * (volume - 0.5f) + midVolume;
            }
            volumeControl.setValue(dB);
        }
    }

    public double getDurationInSeconds() {
        return durationInSeconds;
    }
}
