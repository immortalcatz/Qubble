package net.ilexiconn.qubble.server.model.exporter;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureMapExporter implements IModelExporter<BufferedImage> {
    @Override
    public String getName() {
        return "Texture Map";
    }

    @Override
    public String getExtension() {
        return "png";
    }

    @Override
    public BufferedImage export(QubbleModel model, String... arguments) {
        model.unparent();
        BufferedImage texture = new BufferedImage(model.getTextureWidth(), model.getTextureHeight(), BufferedImage.TYPE_INT_ARGB);
        for (QubbleCuboid cube : model.getCuboids()) {
            int textureX = cube.getTextureX();
            int textureY = cube.getTextureY();
            int dimensionX = cube.getDimensionX();
            int dimensionY = cube.getDimensionY();
            int dimensionZ = cube.getDimensionZ();
            this.fill(texture, textureX + dimensionZ, textureY, dimensionX, dimensionZ, 0xFF00FF00);
            this.fill(texture, textureX + dimensionX + dimensionZ, textureY, dimensionX, dimensionZ, 0xFF00AA00);
            this.fill(texture, textureX, textureY + dimensionZ, dimensionZ, dimensionY, 0xFFFF0000);
            this.fill(texture, textureX + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, 0xFF0000FF);
            this.fill(texture, textureX + dimensionX + dimensionZ, textureY + dimensionZ, dimensionZ, dimensionY, 0xFFAA0000);
            this.fill(texture, textureX + dimensionX + dimensionZ + dimensionZ, textureY + dimensionZ, dimensionX, dimensionY, 0xFF0000AA);
        }
        return texture;
    }

    @Override
    public void save(BufferedImage model, File file) throws IOException {
        ImageIO.write(model, this.getExtension(), file);
    }

    @Override
    public String[] getArgumentNames() {
        return new String[]{};
    }

    @Override
    public String[] getDefaultArguments(QubbleModel currentModel) {
        return new String[]{};
    }

    private void fill(BufferedImage image, int x, int y, int width, int height, int color) {
        for (int textureX = x; textureX < x + width; textureX++) {
            for (int textureY = y; textureY < y + height; textureY++) {
                if (textureX >= 0 && textureY >= 0 && textureX < image.getWidth() && textureY < image.getHeight()) {
                    image.setRGB(textureX, textureY, color);
                }
            }
        }
    }
}