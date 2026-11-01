package net.voxla.growlingman.port;

import software.bernie.geckolib.renderer.base.GeoRenderState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.animation.state.AnimationTest;
import software.bernie.geckolib.animation.object.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animatable.GeoAnimatable;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.Set;

/**
 * GeckoLib 5 helpers for the MCreator entities.
 * <p>
 * {@link #procedure}: the "procedure" animation controller plays the animation a procedure asked for (setAnimation,
 * synced to the client and stored in animationprocedure) once, then goes back to "empty" so the movement controller
 * takes over again. Same behaviour as the GeckoLib 4 code of 3.3.7.
 * <p>
 * {@link Controller}: 3.3.7 asks for a few animations its files do not have (the Faker watcher's "idle", the Hider's
 * "swim8", a procedure's "releport1122"). GeckoLib 4 skipped them; GeckoLib 5.4 crashes the client while building the
 * animation timeline (NoSuchElementException in AnimationTimeline.create when no stage is found and the controller has
 * transition ticks). The controller skips an animation that is not in the file, as before, and remembers it, so
 * GeckoLib logs it once instead of every frame.
 */
public final class GeoAnims {
	private static final Set<String> MISSING = ConcurrentHashMap.newKeySet();

	private GeoAnims() {
	}

	public static <T extends GeoAnimatable> PlayState procedure(AnimationTest<T> test, String animation, Consumer<String> setAnimation) {
		if (animation.equals("empty"))
			return PlayState.STOP;
		AnimationController<T> controller = test.controller();
		RawAnimation raw = RawAnimation.begin().thenPlay(animation);
		if (controller instanceof Controller<T> safe && raw.equals(safe.skipped)) {
			safe.skipped = null;
			setAnimation.accept("empty");
			return PlayState.STOP;
		}
		if (!raw.equals(controller.getCurrentRawAnimation())) {
			controller.setAnimation(raw);
			return PlayState.CONTINUE;
		}
		if (controller.hasAnimationFinished()) {
			setAnimation.accept("empty");
			controller.reset();
			return PlayState.STOP;
		}
		return PlayState.CONTINUE;
	}

	public static final class Controller<T extends GeoAnimatable> extends AnimationController<T> {
		private RawAnimation skipped;

		public Controller(String name, int transitionTicks, AnimationStateHandler<T> stateHandler) {
			super(name, transitionTicks, stateHandler);
		}

		@Override
		protected void initializeNewAnimation(T animatable, GeoRenderState renderState, GeoModel<T> geoModel, double prevAnimSpeed, int prevTransitionTicks) {
			RawAnimation raw = getCurrentRawAnimation();
			if (raw != null && !anyExists(raw, animatable, geoModel)) {
				this.skipped = raw;
				reset();
				return;
			}
			super.initializeNewAnimation(animatable, renderState, geoModel, prevAnimSpeed, prevTransitionTicks);
		}

		private static <T extends GeoAnimatable> boolean anyExists(RawAnimation raw, T animatable, GeoModel<T> geoModel) {
			String file = geoModel.getAnimationResource(animatable) + "#";
			for (RawAnimation.Stage stage : raw.getAnimationStages()) {
				//noinspection StringEquality
				if (stage.animationName() == RawAnimation.Stage.WAIT)
					return true;
				String key = file + stage.animationName();
				if (MISSING.contains(key))
					continue;
				if (geoModel.getBakedAnimation(animatable, stage.animationName()) != null)
					return true;
				MISSING.add(key);
			}
			return false;
		}
	}
}
