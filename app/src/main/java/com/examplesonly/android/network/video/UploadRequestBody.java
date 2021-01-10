package com.examplesonly.android.network.video;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class UploadRequestBody extends RequestBody {

    private final RequestBody delegate;
    private final UploadCallbacks listener;

    public UploadRequestBody(RequestBody delegate, UploadCallbacks listener) {
        this.delegate = delegate;
        this.listener = listener;
    }


    public interface UploadCallbacks {
        void onProgressUpdate(double progress, long bytesWritten, long contentLength);

        void onError();

        void onFinish();
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        CountingSink countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;

        CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(@NonNull Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            bytesWritten += byteCount;
            double progress = (1.0 * bytesWritten) / contentLength();
            listener.onProgressUpdate(progress, bytesWritten, contentLength());
        }
    }
}



