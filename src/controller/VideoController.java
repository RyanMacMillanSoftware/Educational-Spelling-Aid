/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009, 2010, 2011, 2012, 2013, 2014, 2015 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.javafx.test;

import java.nio.ByteBuffer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritablePixelFormat;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DefaultDirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

import com.sun.jna.Memory;

/**
 * Example showing how to render video to a JavaFX Canvas component.
 * <p>
 * The target is to render full HD video (1920x1080) at a reasonable frame rate (>25fps).
 * <p>
 * This test can render the video at a fixed size, or it can take the size from the
 * video itself.
 * <p>
 * You may need to set -Djna.library.path=[path-to-libvlc] on the command-line.
 * <p>
 * Originally based on an example contributed by John Hendrikx.
 * <p>
 * -Dprism.verbose=true -Xmx512m -verbose:gc
 * <p>
 * This version works with JavaFX on JDK 1.8, without "wrong thread" errors.
 */
public abstract class VideoController extends Application {

    /**
     * Filename of the video to play.
     */
    private static final String VIDEO_FILE = "resources/big_buck_bunny_1_minute.avi";

    /**
     * Set this to <code>true</code> to resize the display to the dimensions of the
     * video, otherwise it will use {@link #WIDTH} and {@link #HEIGHT}.
     */
    private static final boolean useSourceSize = true;

    /**
     * Target width, unless {@link #useSourceSize} is set.
     */
    private static final int WIDTH = 720;

    /**
     * Target height, unless {@link #useSourceSize} is set.
     */
    private static final int HEIGHT = 576;

    /**
     * Lightweight JavaFX canvas, the video is rendered here.
     */
    private final Canvas canvas;

    /**
     * Pixel writer to update the canvas.
     */
    private final PixelWriter pixelWriter;

    /**
     * Pixel format.
     */
    private final WritablePixelFormat<ByteBuffer> pixelFormat;

    /**
     *
     */
    private final BorderPane borderPane;

    /**
     * The vlcj direct rendering media player component.
     */
    private final DirectMediaPlayerComponent mediaPlayerComponent;

    /**
     *
     */
    private Stage stage;

    /**
     *
     */
    private Scene scene;

    /**
     *
     */
    public VideoController() {
        canvas = new Canvas();

        pixelWriter = canvas.getGraphicsContext2D().getPixelWriter();
        pixelFormat = PixelFormat.getByteBgraInstance();

        borderPane = new BorderPane();
        borderPane.setCenter(canvas);

        mediaPlayerComponent = new TestMediaPlayerComponent();
    }

    @Override
    public final void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        stage.setTitle("Congratulations");

        scene = new Scene(borderPane);

        primaryStage.setScene(scene);
        primaryStage.show();

        mediaPlayerComponent.getMediaPlayer().playMedia(VIDEO_FILE);

        mediaPlayerComponent.getMediaPlayer().setPosition(0.7f);

        startTimer();
    }

    @Override
    public final void stop() throws Exception {
        stopTimer();

        mediaPlayerComponent.getMediaPlayer().stop();
        mediaPlayerComponent.getMediaPlayer().release();
    }

    /**
     * Implementation of a direct rendering media player component that renders
     * the video to a JavaFX canvas.
     */
    private class TestMediaPlayerComponent extends DirectMediaPlayerComponent {

        public TestMediaPlayerComponent() {
            super(new TestBufferFormatCallback());
        }
    }

    /**
     * Callback to get the buffer format to use for video playback.
     */
    private class TestBufferFormatCallback implements BufferFormatCallback {

        @Override
        public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
            final int width;
            final int height;
            if (useSourceSize) {
                width = sourceWidth;
                height = sourceHeight;
            }
            else {
                width = WIDTH;
                height = HEIGHT;
            }
            Platform.runLater(new Runnable () {
                @Override
                public void run() {
                    canvas.setWidth(width);
                    canvas.setHeight(height);
                    stage.setWidth(width);
                    stage.setHeight(height);
                }
            });
            return new RV32BufferFormat(width, height);
        }
    }

    /**
     *
     */
    protected final void renderFrame() {
        Memory[] nativeBuffers = mediaPlayerComponent.getMediaPlayer().lock();
        if (nativeBuffers != null) {
            // FIXME there may be more efficient ways to do this...
            // Since this is now being called by a specific rendering time, independent of the native video callbacks being
            // invoked, some more defensive conditional checks are needed
            Memory nativeBuffer = nativeBuffers[0];
            if (nativeBuffer != null) {
                ByteBuffer byteBuffer = nativeBuffer.getByteBuffer(0, nativeBuffer.size());
                BufferFormat bufferFormat = ((DefaultDirectMediaPlayer) mediaPlayerComponent.getMediaPlayer()).getBufferFormat();
                if (bufferFormat.getWidth() > 0 && bufferFormat.getHeight() > 0) {
                    pixelWriter.setPixels(0, 0, bufferFormat.getWidth(), bufferFormat.getHeight(), pixelFormat, byteBuffer, bufferFormat.getPitches()[0]);
                }
            }
        }
        mediaPlayerComponent.getMediaPlayer().unlock();
    }

    /**
     *
     */
    protected abstract void startTimer();

    /**
     *
     */
    protected abstract void stopTimer();
}