package net.ilexiconn.qubble.server.model.exporter;

import net.ilexiconn.llibrary.client.model.qubble.QubbleCuboid;
import net.ilexiconn.llibrary.client.model.qubble.QubbleModel;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class KotlinExporter implements IModelExporter<List<String>> {
    @Override
    public String getName() {
        return "Kotlin";
    }

    @Override
    public String getExtension() {
        return "kt";
    }

    @Override
    public List<String> export(QubbleModel model, String... arguments) {
        List<String> list = new ArrayList<>();
        list.add("package " + arguments[0]);
        list.add("");
        list.add("import net.minecraft.client.model.ModelBase");
        list.add("import net.minecraft.client.model.ModelRenderer");
        list.add("import net.minecraft.client.renderer.GlStateManager");
        list.add("import net.minecraft.entity.Entity");
        list.add("import net.minecraftforge.fml.relauncher.Side");
        list.add("import net.minecraftforge.fml.relauncher.SideOnly");
        list.add("import org.lwjgl.opengl.GL11");
        list.add("");
        list.add("/**");
        list.add(" * " + model.getName() + " by " + model.getAuthor());
        list.add(" */");
        list.add("@SideOnly(Side.CLIENT)");
        list.add("class " + arguments[1] + "() : ModelBase() {");
        this.addCubeFields(model.getCuboids(), list);
        list.add("");
        list.add("    init {");
        list.add("        textureWidth = " + model.getTextureWidth());
        list.add("        textureHeight = " + model.getTextureHeight());
        list.add("");
        this.addCubeDeclarations(model.getCuboids(), null, list);
        list.add("    }");
        list.add("");
        list.add("    override fun render(entity: Entity, limbSwing: Float, limbSwingAmount: Float, ageInTick: Float, rotationYaw: Float, rotationPitch: Float, scale: Float) {");
        this.addRenderCalls(model.getCuboids(), list);
        list.add("    }");
        list.add("");
        this.addRotationAngles(list);
        list.add("}");
        return list;
    }

    @Override
    public void save(List<String> model, File file) throws IOException {
        PrintWriter writer = new PrintWriter(file, "UTF-8");
        model.forEach(writer::println);
        writer.close();
    }

    public void addCubeFields(List<QubbleCuboid> cubes, List<String> list) {
        for (QubbleCuboid cube : cubes) {
            list.add("    var " + this.getFieldName(cube) + ": ModelRenderer");
            this.addCubeFields(cube.getChildren(), list);
        }
    }

    public void addCubeDeclarations(List<QubbleCuboid> cubes, QubbleCuboid parent, List<String> list) {
        for (QubbleCuboid cube : cubes) {
            String field = this.getFieldName(cube);
            list.add("        this." + field + " = new ModelRenderer(this, " + cube.getTextureX() + ", " + cube.getTextureY() + ").apply {");
            list.add("            setRotationPoint(" + cube.getPositionX() + "F, " + cube.getPositionY() + "F, " + cube.getPositionZ() + "F)");
            list.add("            addBox(" + cube.getOffsetX() + "F, " + cube.getOffsetY() + "F, " + cube.getOffsetZ() + "F, " + cube.getDimensionX() + ", " + cube.getDimensionY() + ", " + cube.getDimensionZ() + ")");
            if (cube.isTextureMirrored()) {
                list.add("            mirror = true");
            }
            list.add("        }");
            if (cube.getRotationX() != 0.0F || cube.getRotationY() != 0.0F || cube.getRotationZ() != 0.0F) {
                list.add("        this.setRotationAngles(this." + field + ", " + Math.toRadians(cube.getRotationX()) + "F, " + Math.toRadians(cube.getRotationY()) + "F, " + Math.toRadians(cube.getRotationZ()) + "F)");
            }
            if (parent != null) {
                list.add("        this." + this.getFieldName(parent) + ".addChild(this." + field + ")");
            }
            this.addCubeDeclarations(cube.getChildren(), cube, list);
        }
    }

    public void addRenderCalls(List<QubbleCuboid> cubes, List<String> list) {
        for (QubbleCuboid cube : cubes) {
            String field = this.getFieldName(cube);
            boolean scale = cube.getRotationX() != 0.0F || cube.getRotationY() != 0.0F || cube.getRotationZ() != 0.0F;
            boolean blend = cube.getOpacity() != 100.0F;
            if (scale) {
                list.add("        GlStateManager.pushMatrix()");
                list.add("        GlStateManager.translate(this." + field + ".offsetX, this." + field + ".offsetY, this." + field + ".offsetZ)");
                list.add("        GlStateManager.translate(this." + field + ".rotationPointX * scale, this." + field + ".rotationPointY * scale, this." + field + ".rotationPointZ * scale)");
                list.add("        GlStateManager.scale(" + cube.getScaleX() + "F, " + cube.getScaleY() + "F, " + cube.getScaleZ() + "F)");
                list.add("        GlStateManager.translate(-this." + field + ".offsetX, -this." + field + ".offsetY, -this." + field + ".offsetZ)");
                list.add("        GlStateManager.translate(-this." + field + ".rotationPointX * scale, -this." + field + ".rotationPointY * scale, -this." + field + ".rotationPointZ * scale)");
            }
            if (blend) {
                list.add("        GlStateManager.enableBlend()");
                list.add("        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)");
                list.add("        GlStateManager.color(1.0F, 1.0F, 1.0F, " + (cube.getOpacity() / 100.0F) + "F)");
            }
            list.add("        this." + field + ".render(scale)");
            if (blend) {
                list.add("        GlStateManager.disableBlend()");
            }
            if (scale) {
                list.add("        GlStateManager.popMatrix()");
            }
        }
    }

    public void addRotationAngles(List<String> list) {
        list.add("    fun setRotationAngles(modelRenderer: ModelRenderer, x: Float, y: Float, z: Float) {");
        list.add("        modelRenderer.rotateAngleX = x");
        list.add("        modelRenderer.rotateAngleY = y");
        list.add("        modelRenderer.rotateAngleZ = z");
        list.add("    }");
    }

    public String getFieldName(QubbleCuboid cube) {
        return cube.getName().replaceAll("[^A-Za-z0-9_$]", "");
    }

    @Override
    public String[] getArgumentNames() {
        return new String[]{"Package", "Class Name"};
    }

    @Override
    public String[] getDefaultArguments(QubbleModel currentModel) {
        return new String[]{"pkg", currentModel.getName()};
    }
}
