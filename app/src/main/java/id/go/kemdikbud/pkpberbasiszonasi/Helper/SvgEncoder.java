package id.go.kemdikbud.pkpberbasiszonasi.Helper;

import android.graphics.Picture;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.EncodeStrategy;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceEncoder;
import com.bumptech.glide.load.engine.Resource;
import com.caverock.androidsvg.SVG;

import java.io.File;
import java.io.FileOutputStream;

public class SvgEncoder implements ResourceEncoder<SVG> {
    @NonNull
    @Override
    public EncodeStrategy getEncodeStrategy(@NonNull Options options) {
        return EncodeStrategy.SOURCE;
    }

    @Override
    public boolean encode(@NonNull Resource<SVG> data, @NonNull File file, @NonNull Options options) {
        try {
            SVG svg = data.get();
            Picture picture = svg.renderToPicture();
            picture.writeToStream(new FileOutputStream(file));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
