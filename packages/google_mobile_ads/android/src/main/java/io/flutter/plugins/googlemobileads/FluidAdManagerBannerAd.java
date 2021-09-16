// Copyright 2021 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package io.flutter.plugins.googlemobileads;

import android.view.View;
import android.view.View.OnLayoutChangeListener;
import android.widget.ScrollView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.ads.AdSize;
import io.flutter.plugin.platform.PlatformView;
import java.util.Collections;
import java.util.List;

/** A subclass of {@link FlutterAdManagerBannerAd} specifically for fluid ad size. */
final class FluidAdManagerBannerAd extends FlutterAdManagerBannerAd {

  @Nullable private ScrollView containerView;

  private int height = -1;

  FluidAdManagerBannerAd(
      int adId,
      @NonNull AdInstanceManager manager,
      @NonNull String adUnitId,
      @NonNull List<FlutterAdSize> sizes,
      @NonNull FlutterAdManagerAdRequest request,
      @NonNull BannerAdCreator bannerAdCreator
  ) {
    super(
        adId,
        manager,
        adUnitId,
        Collections.singletonList(new FlutterAdSize(AdSize.FLUID)),
        request,
        bannerAdCreator);
  }

  @Override
  public void onAdLoaded() {
    if (adView != null) {
      adView.addOnLayoutChangeListener(new OnLayoutChangeListener() {
        @Override
        public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
            int oldTop, int oldRight, int oldBottom) {
          // Forward the new height to its container.
          if (v.getMeasuredHeight() != height) {
            manager.onFluidAdHeightChanged(adId, height);
          }
          height = v.getMeasuredHeight();
        }
      });
      manager.onAdLoaded(adId, adView.getResponseInfo());
    }
  }

  @Nullable
  @Override
  PlatformView getPlatformView() {
    if (adView == null) {
      return null;
    }
    if (containerView != null) {
      return new FlutterPlatformView(containerView);
    }
    // Place the ad view inside a scroll view. This allows the height of the ad view to overflow
    // it container so we can calculate the height and send it back to flutter.
    containerView = new ScrollView(manager.activity);
    containerView.addView(adView);
    return new FlutterPlatformView(adView);
  }
}