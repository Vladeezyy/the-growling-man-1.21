package net.voxla.growlingman.entity;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import com.geckolib.util.GeckoLibUtil;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.RawAnimation;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.constant.DataTickets;
import net.voxla.growlingman.port.GeoAnims;
import net.minecraft.server.level.ServerLevel;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RandomSwimmingGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.voxla.growlingman.init.GrowlingManModEntities;
import net.voxla.growlingman.port.bus.GMBus;
import net.voxla.growlingman.procedures.GMCanAttackCondProcedure;
import net.voxla.growlingman.procedures.GMWanderCondProcedure;
import net.voxla.growlingman.procedures.GrowlingManAttackRaidProcProcedure;
import net.voxla.growlingman.procedures.GrowlingManBoundingBoxScaleProcedure;
import net.voxla.growlingman.procedures.GrowlingManTickHandlerProcedure;

public class GrowlingManEntity extends Monster implements GeoEntity {
	public static final EntityDataAccessor<Boolean> SHOOT = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> ANIMATION = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> TEXTURE = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Boolean> DATA_Chasing = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_IsBeingSeen = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_IsBeingLookedAt = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_CanSeePlayer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_Attacking = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_AI = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DespawnOverTimeTimer = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_ChaseTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_Climbing = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_AmbientTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<String> DATA_WalkTarget = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> DATA_ChaseHurtTarget = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_ChaseHurtTargetTimer = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_BreakDoorTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_BreakDoorAmount = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ManifestTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_DeManifestTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_PlayerNorth = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_PlayerSouth = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_PlayerEast = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Boolean> DATA_PlayerWest = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_ChaseSection = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ChaseAmountRandomizer = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_RunToX = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_RunToZ = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_LifeSpanTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_PlayEventAnimation = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.BOOLEAN
	);
	public static final EntityDataAccessor<String> DATA_RunningAnimation = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> DATA_WalkingAnimation = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> DATA_IdleAnimation = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<String> DATA_EventAnimation = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	public static final EntityDataAccessor<Integer> DATA_HorizontalClimb = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_Transparency = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_HorizontalClimbing = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.BOOLEAN
	);
	public static final EntityDataAccessor<Integer> DATA_GrabbingPlayer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_SmackPlayerOffPillar = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_GrabPlayerCooldown = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_DeManifestType = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ChaseAmbientTimer = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_ConcealSelf = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_ConcealSelfTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Integer> DATA_LookedAtEventRandomizer = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_RandomLookEventCooldown = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_RunAtRunAwayEvent = SynchedEntityData.defineId(
		GrowlingManEntity.class, EntityDataSerializers.INT
	);
	public static final EntityDataAccessor<Integer> DATA_AIIdleTimer = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_Enraged = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<Integer> DATA_AggroStared = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.INT);
	public static final EntityDataAccessor<Boolean> DATA_FoldInArms = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.BOOLEAN);
	public static final EntityDataAccessor<String> DATA_GhostTarget = SynchedEntityData.defineId(GrowlingManEntity.class, EntityDataSerializers.STRING);
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private boolean swinging;
	private boolean lastloop;
	private long lastSwing;
	public String animationprocedure = "empty";


	public GrowlingManEntity(EntityType<GrowlingManEntity> type, Level world) {
		super(type, world);
		this.xpReward = 0;
		this.setNoAi(false);
		this.getAttribute(Attributes.STEP_HEIGHT).setBaseValue(0.6F);
		this.setPersistenceRequired();
	}

	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(SHOOT, false);
		builder.define(ANIMATION, "undefined");
		builder.define(TEXTURE, "growling_man");
		builder.define(DATA_Chasing, false);
		builder.define(DATA_IsBeingSeen, false);
		builder.define(DATA_IsBeingLookedAt, false);
		builder.define(DATA_CanSeePlayer, false);
		builder.define(DATA_Attacking, 0);
		builder.define(DATA_AI, 0);
		builder.define(DATA_DespawnOverTimeTimer, 0);
		builder.define(DATA_ChaseTimer, 0);
		builder.define(DATA_Climbing, false);
		builder.define(DATA_AmbientTimer, 0);
		builder.define(DATA_WalkTarget, "");
		builder.define(DATA_ChaseHurtTarget, "");
		builder.define(DATA_ChaseHurtTargetTimer, 0);
		builder.define(DATA_BreakDoorTimer, 0);
		builder.define(DATA_BreakDoorAmount, 0);
		builder.define(DATA_ManifestTimer, 0);
		builder.define(DATA_DeManifestTimer, 0);
		builder.define(DATA_PlayerNorth, false);
		builder.define(DATA_PlayerSouth, false);
		builder.define(DATA_PlayerEast, false);
		builder.define(DATA_PlayerWest, false);
		builder.define(DATA_ChaseSection, 0);
		builder.define(DATA_ChaseAmountRandomizer, 0);
		builder.define(DATA_RunToX, 0);
		builder.define(DATA_RunToZ, 0);
		builder.define(DATA_LifeSpanTimer, 0);
		builder.define(DATA_PlayEventAnimation, false);
		builder.define(DATA_RunningAnimation, "animation.growling_man.movefast");
		builder.define(DATA_WalkingAnimation, "animation.growling_man.move");
		builder.define(DATA_IdleAnimation, "animation.growling_man.idle");
		builder.define(DATA_EventAnimation, "");
		builder.define(DATA_HorizontalClimb, 0);
		builder.define(DATA_Transparency, 0);
		builder.define(DATA_HorizontalClimbing, false);
		builder.define(DATA_GrabbingPlayer, 0);
		builder.define(DATA_SmackPlayerOffPillar, 0);
		builder.define(DATA_GrabPlayerCooldown, 0);
		builder.define(DATA_DeManifestType, 0);
		builder.define(DATA_ChaseAmbientTimer, 0);
		builder.define(DATA_ConcealSelf, 0);
		builder.define(DATA_ConcealSelfTimer, 0);
		builder.define(DATA_LookedAtEventRandomizer, 0);
		builder.define(DATA_RandomLookEventCooldown, 0);
		builder.define(DATA_RunAtRunAwayEvent, 0);
		builder.define(DATA_AIIdleTimer, 0);
		builder.define(DATA_Enraged, false);
		builder.define(DATA_AggroStared, 0);
		builder.define(DATA_FoldInArms, false);
		builder.define(DATA_GhostTarget, "");
	}

	public void setTexture(String texture) {
		this.entityData.set(TEXTURE, texture);
	}

	public String getTexture() {
		return (String)this.entityData.get(TEXTURE);
	}


	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(1, new NearestAttackableTargetGoal(this, Player.class, true, false) {
			public boolean canUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canUse() && GMCanAttackCondProcedure.execute(entity);
			}

			public boolean canContinueToUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canContinueToUse() && GMCanAttackCondProcedure.execute(entity);
			}
		});
		this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, LivingEntity.class, true, false) {
			public boolean canUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canUse() && GrowlingManAttackRaidProcProcedure.execute(world, x, y, z, entity);
			}
		});
		this.goalSelector.addGoal(3, new MeleeAttackGoal(this, 2.7, false) {
			protected boolean canPerformAttack(LivingEntity entity) {
				return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < 6.76 && this.mob.getSensing().hasLineOfSight(entity);
			}
		});
		this.goalSelector.addGoal(4, new RandomStrollGoal(this, 1.0) {
			public boolean canUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canUse() && GMWanderCondProcedure.execute(entity);
			}

			public boolean canContinueToUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canContinueToUse() && GMWanderCondProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(5, new RandomSwimmingGoal(this, 1.0, 40) {
			public boolean canUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canUse() && GMCanAttackCondProcedure.execute(entity);
			}

			public boolean canContinueToUse() {
				double x = GrowlingManEntity.this.getX();
				double y = GrowlingManEntity.this.getY();
				double z = GrowlingManEntity.this.getZ();
				Entity entity = GrowlingManEntity.this;
				Level world = GrowlingManEntity.this.level();
				return super.canContinueToUse() && GMCanAttackCondProcedure.execute(entity);
			}
		});
		this.goalSelector.addGoal(6, new FloatGoal(this));
	}


	public boolean removeWhenFarAway(double distanceToClosestPlayer) {
		return false;
	}

	public void playStepSound(BlockPos pos, BlockState blockIn) {
		this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.step")), 0.15F, 1.0F);
	}

	public SoundEvent getHurtSound(DamageSource ds) {
		return (SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.hurt"));
	}

	public SoundEvent getDeathSound() {
		return (SoundEvent)BuiltInRegistries.SOUND_EVENT.getValue(Identifier.parse("growling_man:entity.growling_man.death"));
	}

	public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
		if (source.is(DamageTypes.CACTUS)) {
			return false;
		} else if (source.is(DamageTypes.LIGHTNING_BOLT)) {
			return false;
		} else if (source.is(DamageTypes.DRAGON_BREATH)) {
			return false;
		} else if (source.is(DamageTypes.WITHER)) {
			return false;
		} else {
			return source.is(DamageTypes.WITHER_SKULL) ? false : super.hurtServer(level, source, amount);
		}
	}

	public void addAdditionalSaveData(ValueOutput compound) {
		super.addAdditionalSaveData(compound);
		compound.putString("Texture", this.getTexture());
		compound.putBoolean("DataChasing", (Boolean)this.entityData.get(DATA_Chasing));
		compound.putBoolean("DataIsBeingSeen", (Boolean)this.entityData.get(DATA_IsBeingSeen));
		compound.putBoolean("DataIsBeingLookedAt", (Boolean)this.entityData.get(DATA_IsBeingLookedAt));
		compound.putBoolean("DataCanSeePlayer", (Boolean)this.entityData.get(DATA_CanSeePlayer));
		compound.putInt("DataAttacking", (Integer)this.entityData.get(DATA_Attacking));
		compound.putInt("DataAI", (Integer)this.entityData.get(DATA_AI));
		compound.putInt("DataDespawnOverTimeTimer", (Integer)this.entityData.get(DATA_DespawnOverTimeTimer));
		compound.putInt("DataChaseTimer", (Integer)this.entityData.get(DATA_ChaseTimer));
		compound.putBoolean("DataClimbing", (Boolean)this.entityData.get(DATA_Climbing));
		compound.putInt("DataAmbientTimer", (Integer)this.entityData.get(DATA_AmbientTimer));
		compound.putString("DataWalkTarget", (String)this.entityData.get(DATA_WalkTarget));
		compound.putString("DataChaseHurtTarget", (String)this.entityData.get(DATA_ChaseHurtTarget));
		compound.putInt("DataChaseHurtTargetTimer", (Integer)this.entityData.get(DATA_ChaseHurtTargetTimer));
		compound.putInt("DataBreakDoorTimer", (Integer)this.entityData.get(DATA_BreakDoorTimer));
		compound.putInt("DataBreakDoorAmount", (Integer)this.entityData.get(DATA_BreakDoorAmount));
		compound.putInt("DataManifestTimer", (Integer)this.entityData.get(DATA_ManifestTimer));
		compound.putInt("DataDeManifestTimer", (Integer)this.entityData.get(DATA_DeManifestTimer));
		compound.putBoolean("DataPlayerNorth", (Boolean)this.entityData.get(DATA_PlayerNorth));
		compound.putBoolean("DataPlayerSouth", (Boolean)this.entityData.get(DATA_PlayerSouth));
		compound.putBoolean("DataPlayerEast", (Boolean)this.entityData.get(DATA_PlayerEast));
		compound.putBoolean("DataPlayerWest", (Boolean)this.entityData.get(DATA_PlayerWest));
		compound.putInt("DataChaseSection", (Integer)this.entityData.get(DATA_ChaseSection));
		compound.putInt("DataChaseAmountRandomizer", (Integer)this.entityData.get(DATA_ChaseAmountRandomizer));
		compound.putInt("DataRunToX", (Integer)this.entityData.get(DATA_RunToX));
		compound.putInt("DataRunToZ", (Integer)this.entityData.get(DATA_RunToZ));
		compound.putInt("DataLifeSpanTimer", (Integer)this.entityData.get(DATA_LifeSpanTimer));
		compound.putBoolean("DataPlayEventAnimation", (Boolean)this.entityData.get(DATA_PlayEventAnimation));
		compound.putString("DataRunningAnimation", (String)this.entityData.get(DATA_RunningAnimation));
		compound.putString("DataWalkingAnimation", (String)this.entityData.get(DATA_WalkingAnimation));
		compound.putString("DataIdleAnimation", (String)this.entityData.get(DATA_IdleAnimation));
		compound.putString("DataEventAnimation", (String)this.entityData.get(DATA_EventAnimation));
		compound.putInt("DataHorizontalClimb", (Integer)this.entityData.get(DATA_HorizontalClimb));
		compound.putInt("DataTransparency", (Integer)this.entityData.get(DATA_Transparency));
		compound.putBoolean("DataHorizontalClimbing", (Boolean)this.entityData.get(DATA_HorizontalClimbing));
		compound.putInt("DataGrabbingPlayer", (Integer)this.entityData.get(DATA_GrabbingPlayer));
		compound.putInt("DataSmackPlayerOffPillar", (Integer)this.entityData.get(DATA_SmackPlayerOffPillar));
		compound.putInt("DataGrabPlayerCooldown", (Integer)this.entityData.get(DATA_GrabPlayerCooldown));
		compound.putInt("DataDeManifestType", (Integer)this.entityData.get(DATA_DeManifestType));
		compound.putInt("DataChaseAmbientTimer", (Integer)this.entityData.get(DATA_ChaseAmbientTimer));
		compound.putInt("DataConcealSelf", (Integer)this.entityData.get(DATA_ConcealSelf));
		compound.putInt("DataConcealSelfTimer", (Integer)this.entityData.get(DATA_ConcealSelfTimer));
		compound.putInt("DataLookedAtEventRandomizer", (Integer)this.entityData.get(DATA_LookedAtEventRandomizer));
		compound.putInt("DataRandomLookEventCooldown", (Integer)this.entityData.get(DATA_RandomLookEventCooldown));
		compound.putInt("DataRunAtRunAwayEvent", (Integer)this.entityData.get(DATA_RunAtRunAwayEvent));
		compound.putInt("DataAIIdleTimer", (Integer)this.entityData.get(DATA_AIIdleTimer));
		compound.putBoolean("DataEnraged", (Boolean)this.entityData.get(DATA_Enraged));
		compound.putInt("DataAggroStared", (Integer)this.entityData.get(DATA_AggroStared));
		compound.putBoolean("DataFoldInArms", (Boolean)this.entityData.get(DATA_FoldInArms));
		compound.putString("DataGhostTarget", (String)this.entityData.get(DATA_GhostTarget));
	}

	public void readAdditionalSaveData(ValueInput compound) {
		super.readAdditionalSaveData(compound);
		compound.getString("Texture").ifPresent(this::setTexture);

		compound.read("DataChasing", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_Chasing, value));

		compound.read("DataIsBeingSeen", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_IsBeingSeen, value));

		compound.read("DataIsBeingLookedAt", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_IsBeingLookedAt, value));

		compound.read("DataCanSeePlayer", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_CanSeePlayer, value));

		compound.read("DataAttacking", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_Attacking, value));

		compound.read("DataAI", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_AI, value));

		compound.read("DataDespawnOverTimeTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_DespawnOverTimeTimer, value));

		compound.read("DataChaseTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ChaseTimer, value));

		compound.read("DataClimbing", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_Climbing, value));

		compound.read("DataAmbientTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_AmbientTimer, value));

		compound.read("DataWalkTarget", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_WalkTarget, value));

		compound.read("DataChaseHurtTarget", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_ChaseHurtTarget, value));

		compound.read("DataChaseHurtTargetTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ChaseHurtTargetTimer, value));

		compound.read("DataBreakDoorTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_BreakDoorTimer, value));

		compound.read("DataBreakDoorAmount", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_BreakDoorAmount, value));

		compound.read("DataManifestTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ManifestTimer, value));

		compound.read("DataDeManifestTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_DeManifestTimer, value));

		compound.read("DataPlayerNorth", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_PlayerNorth, value));

		compound.read("DataPlayerSouth", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_PlayerSouth, value));

		compound.read("DataPlayerEast", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_PlayerEast, value));

		compound.read("DataPlayerWest", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_PlayerWest, value));

		compound.read("DataChaseSection", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ChaseSection, value));

		compound.read("DataChaseAmountRandomizer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ChaseAmountRandomizer, value));

		compound.read("DataRunToX", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_RunToX, value));

		compound.read("DataRunToZ", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_RunToZ, value));

		compound.read("DataLifeSpanTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_LifeSpanTimer, value));

		compound.read("DataPlayEventAnimation", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_PlayEventAnimation, value));

		compound.read("DataRunningAnimation", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_RunningAnimation, value));

		compound.read("DataWalkingAnimation", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_WalkingAnimation, value));

		compound.read("DataIdleAnimation", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_IdleAnimation, value));

		compound.read("DataEventAnimation", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_EventAnimation, value));

		compound.read("DataHorizontalClimb", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_HorizontalClimb, value));

		compound.read("DataTransparency", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_Transparency, value));

		compound.read("DataHorizontalClimbing", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_HorizontalClimbing, value));

		compound.read("DataGrabbingPlayer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_GrabbingPlayer, value));

		compound.read("DataSmackPlayerOffPillar", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_SmackPlayerOffPillar, value));

		compound.read("DataGrabPlayerCooldown", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_GrabPlayerCooldown, value));

		compound.read("DataDeManifestType", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_DeManifestType, value));

		compound.read("DataChaseAmbientTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ChaseAmbientTimer, value));

		compound.read("DataConcealSelf", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ConcealSelf, value));

		compound.read("DataConcealSelfTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_ConcealSelfTimer, value));

		compound.read("DataLookedAtEventRandomizer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_LookedAtEventRandomizer, value));

		compound.read("DataRandomLookEventCooldown", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_RandomLookEventCooldown, value));

		compound.read("DataRunAtRunAwayEvent", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_RunAtRunAwayEvent, value));

		compound.read("DataAIIdleTimer", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_AIIdleTimer, value));

		compound.read("DataEnraged", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_Enraged, value));

		compound.read("DataAggroStared", com.mojang.serialization.Codec.INT).ifPresent(value -> this.entityData.set(DATA_AggroStared, value));

		compound.read("DataFoldInArms", com.mojang.serialization.Codec.BOOL).ifPresent(value -> this.entityData.set(DATA_FoldInArms, value));

		compound.read("DataGhostTarget", com.mojang.serialization.Codec.STRING).ifPresent(value -> this.entityData.set(DATA_GhostTarget, value));
	}

	@Override
	public void tick() {
		// 1.0.1 did this in a LivingTickEvent listener for every living entity (EntityAnimationFactory)
		String animation = this.getSyncedAnimation();
		if (!animation.equals("undefined")) {
			this.setAnimation("undefined");
			this.animationprocedure = animation;
		}
		super.tick();
	}

	public void baseTick() {
		super.baseTick();
		long start = GMBus.profiling ? System.nanoTime() : 0;
		GrowlingManTickHandlerProcedure.execute(this.level(), this.getX(), this.getY(), this.getZ(), this);
		this.refreshDimensions();
		if (start != 0)
			GMBus.record("GrowlingManEntity tick handler", System.nanoTime() - start);
	}

	public EntityDimensions getDefaultDimensions(Pose p_33597_) {
		Level world = this.level();
		double x = this.getX();
		double y = this.getY();
		double z = this.getZ();
		return super.getDefaultDimensions(p_33597_).scale((float)GrowlingManBoundingBoxScaleProcedure.execute(world, x, y, z, this));
	}

	public static Builder createAttributes() {
		Builder builder = Mob.createMobAttributes();
		builder = builder.add(Attributes.MOVEMENT_SPEED, 0.3);
		builder = builder.add(Attributes.MAX_HEALTH, 200.0);
		builder = builder.add(Attributes.ARMOR, 13.0);
		builder = builder.add(Attributes.ATTACK_DAMAGE, 1.0);
		builder = builder.add(Attributes.FOLLOW_RANGE, 16.0);
		return builder.add(Attributes.KNOCKBACK_RESISTANCE, 0.3);
	}

	private PlayState movementPredicate(AnimationTest<GrowlingManEntity> event) {
		if (this.animationprocedure.equals("empty")) {
			if ((Boolean)this.entityData.get(DATA_PlayEventAnimation)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_EventAnimation)));
			} else if ((event.isMoving() || !(this.walkAnimation.speed() > -0.15F) || !(this.walkAnimation.speed() < 0.15F))
				&& !this.isAggressive()
				&& !this.isInWater()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_WalkingAnimation)));
			} else if (this.isInWater()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.growling_man.swim"));
			} else {
				return this.isAggressive() && event.isMoving() && !this.isInWater()
					? event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_RunningAnimation)))
					: event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_IdleAnimation)));
			}
		} else {
			return PlayState.STOP;
		}
	}

	private PlayState attackingPredicate(AnimationTest<GrowlingManEntity> event) {
		if (this.getSwingAnimation(event.getDataOrDefault(DataTickets.PARTIAL_TICK, 0.0F)) > 0.0F && !this.swinging) {
			this.swinging = true;
			this.lastSwing = this.level().getGameTime();
		}

		if (this.swinging && this.lastSwing + 7L <= this.level().getGameTime()) {
			this.swinging = false;
		}

		if (this.swinging
			&& (Integer)this.entityData.get(DATA_SmackPlayerOffPillar) == 0
			&& (Integer)this.entityData.get(DATA_GrabbingPlayer) == 0
			&& (Integer)this.entityData.get(DATA_DeManifestTimer) == 0
			&& (Integer)this.entityData.get(DATA_ManifestTimer) == 0
			&& (event.controller().getCurrentRawAnimation() == null || event.controller().hasAnimationFinished())) {
			event.controller().reset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.growling_man.attack"));
		} else {
			return PlayState.CONTINUE;
		}
	}

	private PlayState procedurePredicate(AnimationTest<GrowlingManEntity> event) {
		return GeoAnims.procedure(event, this.animationprocedure, animation -> this.animationprocedure = animation);
	}

	protected void tickDeath() {
		this.deathTime++;
		if (this.deathTime == 20) {
			this.remove(RemovalReason.KILLED);
			if (this.level() instanceof ServerLevel _serverLevel)
				this.dropExperience(_serverLevel, null);
		}
	}

	public String getSyncedAnimation() {
		return (String)this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new GeoAnims.Controller<>("movement", 4, this::movementPredicate));
		data.add(new GeoAnims.Controller<>("attacking", 4, this::attackingPredicate));
		data.add(new GeoAnims.Controller<>("procedure", 4, this::procedurePredicate));
	}

	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
