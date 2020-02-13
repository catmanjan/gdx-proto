package org.jrenner.fps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.utils.GdxRuntimeException;
/** utility class that builds on the built-in libgdx GLProfiler */
public class Profiler {
	private static Profiler instance;
	private static GLProfiler glProfiler;
	public static int reportIntervalInFrames = 60;
	private RollingArray drawCalls = new RollingArray();
	private RollingArray calls = new RollingArray();
	private RollingArray shaderSwitches = new RollingArray();
	private RollingArray textureBinds = new RollingArray();
	private RollingArray vertices = new RollingArray();
	private RollingArray fpsCounter = new RollingArray();

	static {
		glProfiler = new GLProfiler(Gdx.graphics);
	}

	public static void enable() {
		instance = new Profiler();
		glProfiler.enable();
	}

	public static void disable() {
		instance = null;
		glProfiler.disable();
	}

	public static void reset() {
		glProfiler.reset();
	}

	public static void tick() {
		if (instance == null) {
			instance = new Profiler();
		}
		instance.drawCalls.add(glProfiler.getDrawCalls());
		instance.calls.add(glProfiler.getCalls());
		instance.shaderSwitches.add(glProfiler.getShaderSwitches());
		instance.textureBinds.add(glProfiler.getTextureBindings());
		instance.vertices.add(glProfiler.getVertexCount().count);
		instance.fpsCounter.add(Gdx.graphics.getFramesPerSecond());
		if (Main.frame % reportIntervalInFrames == 0) {
			Log.debug(instance.reportAverage());
		}
	}

	private String reportAverage() {
		float call = calls.getAverage();
		float draw = drawCalls.getAverage();
		float shade = shaderSwitches.getAverage();
		float tex = textureBinds.getAverage();
		float verts = vertices.getAverage();
		float fps = fpsCounter.getAverage();
		return createReport(fps, call, draw, shade, tex, verts);
	}

	private String createReport(float fps, float call, float draw, float shade, float tex, float verts) {
		return String.format("FPS: %.1f, Call: %.1f, Draw: %.1f, Shader: %.1f, TextureBind: %.1f, Vertices: %.1f",
				fps, call, draw, shade, tex, verts);
	}

	private String reportLast() {
		float call = calls.getItems().peek();
		float draw = drawCalls.getItems().peek();
		float shade = shaderSwitches.getItems().peek();
		float tex = textureBinds.getItems().peek();
		float verts = vertices.getItems().peek();
		float fps = fpsCounter.getItems().peek();
		return createReport(fps, call, draw, shade, tex, verts);
	}

}
