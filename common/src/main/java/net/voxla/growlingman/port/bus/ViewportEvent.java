package net.voxla.growlingman.port.bus;

import com.mojang.blaze3d.shaders.FogShape;

import net.minecraft.client.Camera;
import net.minecraft.client.renderer.FogRenderer;

public abstract class ViewportEvent extends Event {
	private final Camera camera;
	private final double partialTick;

	protected ViewportEvent(Camera camera, double partialTick) {
		this.camera = camera;
		this.partialTick = partialTick;
	}

	public Camera getCamera() {
		return this.camera;
	}

	public double getPartialTick() {
		return this.partialTick;
	}

	/** The fog distances and shape; canceling the event applies the changed values */
	public static class RenderFog extends ViewportEvent implements ICancellableEvent {
		private final FogRenderer.FogMode mode;
		private float nearPlaneDistance;
		private float farPlaneDistance;
		private FogShape fogShape;

		public RenderFog(FogRenderer.FogMode mode, Camera camera, double partialTick, float near, float far, FogShape shape) {
			super(camera, partialTick);
			this.mode = mode;
			this.nearPlaneDistance = near;
			this.farPlaneDistance = far;
			this.fogShape = shape;
		}

		public FogRenderer.FogMode getMode() {
			return this.mode;
		}

		public float getNearPlaneDistance() {
			return this.nearPlaneDistance;
		}

		public void setNearPlaneDistance(float distance) {
			this.nearPlaneDistance = distance;
		}

		public float getFarPlaneDistance() {
			return this.farPlaneDistance;
		}

		public void setFarPlaneDistance(float distance) {
			this.farPlaneDistance = distance;
		}

		public FogShape getFogShape() {
			return this.fogShape;
		}

		public void setFogShape(FogShape shape) {
			this.fogShape = shape;
		}
	}

	public static class ComputeFogColor extends ViewportEvent {
		private float red;
		private float green;
		private float blue;

		public ComputeFogColor(Camera camera, double partialTick, float red, float green, float blue) {
			super(camera, partialTick);
			this.red = red;
			this.green = green;
			this.blue = blue;
		}

		public float getRed() {
			return this.red;
		}

		public void setRed(float red) {
			this.red = red;
		}

		public float getGreen() {
			return this.green;
		}

		public void setGreen(float green) {
			this.green = green;
		}

		public float getBlue() {
			return this.blue;
		}

		public void setBlue(float blue) {
			this.blue = blue;
		}
	}

	/** The field of view of the frame (degrees); setFOV changes it */
	public static class ComputeFov extends ViewportEvent {
		private final boolean usedConfiguredFov;
		private double fov;

		public ComputeFov(Camera camera, double partialTick, double fov, boolean usedConfiguredFov) {
			super(camera, partialTick);
			this.fov = fov;
			this.usedConfiguredFov = usedConfiguredFov;
		}

		public double getFOV() {
			return this.fov;
		}

		public void setFOV(double fov) {
			this.fov = fov;
		}

		public boolean usedConfiguredFOV() {
			return this.usedConfiguredFov;
		}
	}
}
