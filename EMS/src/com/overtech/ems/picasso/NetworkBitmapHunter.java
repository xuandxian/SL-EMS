/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.overtech.ems.picasso;

import static com.overtech.ems.picasso.Picasso.LoadedFrom.DISK;
import static com.overtech.ems.picasso.Picasso.LoadedFrom.NETWORK;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.NetworkInfo;

import com.overtech.ems.picasso.Downloader.Response;

class NetworkBitmapHunter extends BitmapHunter {
  static final int DEFAULT_RETRY_COUNT = 2;
  private static final int MARKER = 65536;

  private final Downloader downloader;

  int retryCount;

  public NetworkBitmapHunter(Picasso picasso, Dispatcher dispatcher, Cache cache, Stats stats,
      Action action, Downloader downloader) {
    super(picasso, dispatcher, cache, stats, action);
    this.downloader = downloader;
    this.retryCount = DEFAULT_RETRY_COUNT;
  }

  @Override Bitmap decode(Request data) throws IOException {
    boolean loadFromLocalCacheOnly = retryCount == 0;

    Response response = downloader.load(data.uri, loadFromLocalCacheOnly);
    if (response == null) {
      return null;
    }

    loadedFrom = response.cached ? DISK : NETWORK;

    Bitmap result = response.getBitmap();
    if (result != null) {
      return result;
    }

    InputStream is = response.getInputStream();
    if (is == null) {
      return null;
    }
    // Sometimes response content length is zero when requests are being replayed. Haven't found
    // root cause to this but retrying the request seems safe to do so.
    if (response.getContentLength() == 0) {
      Utils.closeQuietly(is);
      throw new IOException("Received response with 0 content-length header.");
    }
    if (loadedFrom == NETWORK && response.getContentLength() > 0) {
      stats.dispatchDownloadFinished(response.getContentLength());
    }
    try {
      return decodeStream(is, data,response);
    } finally {
      Utils.closeQuietly(is);
    }
  }

  @Override boolean shouldRetry(boolean airplaneMode, NetworkInfo info) {
    boolean hasRetries = retryCount > 0;
    if (!hasRetries) {
      return false;
    }
    retryCount--;
    return info == null || info.isConnected();
  }

  @Override boolean supportsReplay() {
    return true;
  }

	private Bitmap decodeStream(InputStream stream, Request data,
			Response response) throws IOException {
		//按照需求定制的。
		BufferedInputStream bufferedInputStream = new BufferedInputStream(
				stream);
		final BitmapFactory.Options options = createBitmapOptions(data);
		final boolean calculateSize = requiresInSampleSize(options);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 2];
		int n;
		long over = 0;
		long length = response.getContentLength();
		while (-1 != (n = bufferedInputStream.read(buffer))) {
			byteArrayOutputStream.write(buffer, 0, n);
			over += n;
			if (loadedFrom == NETWORK) {//只有在网络上获取数据的时候才通知进度条
				int progress = (int) ((double) over / (double) length * 100);
				action.changeProgress(progress);
			}
		}
		bufferedInputStream.close();
		byte[] bytes = byteArrayOutputStream.toByteArray();
		if (calculateSize) {
			BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
			calculateInSampleSize(data.targetWidth, data.targetHeight, options);
		}
		//图片缩放
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
		int REQUIRED_SIZE = 480;// 70;
		int width_tmp = options.outWidth, height_tmp = options.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE
					|| height_tmp / 2 < REQUIRED_SIZE)
				break;
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inSampleSize = scale;
		o.inPreferredConfig = Bitmap.Config.RGB_565;
		o.inPurgeable = true;// 允许可清除
		o.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
		
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, o);

	}
}
