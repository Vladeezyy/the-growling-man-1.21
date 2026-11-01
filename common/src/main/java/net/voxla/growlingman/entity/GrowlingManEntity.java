package net.voxla.growlingman.entity;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
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
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController.State;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

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
		this.playSound((SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.step")), 0.15F, 1.0F);
	}

	public SoundEvent getHurtSound(DamageSource ds) {
		return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.hurt"));
	}

	public SoundEvent getDeathSound() {
		return (SoundEvent)BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("growling_man:entity.growling_man.death"));
	}

	public boolean hurt(DamageSource source, float amount) {
		if (source.is(DamageTypes.CACTUS)) {
			return false;
		} else if (source.is(DamageTypes.LIGHTNING_BOLT)) {
			return false;
		} else if (source.is(DamageTypes.DRAGON_BREATH)) {
			return false;
		} else if (source.is(DamageTypes.WITHER)) {
			return false;
		} else {
			return source.is(DamageTypes.WITHER_SKULL) ? false : super.hurt(source, amount);
		}
	}

	public void addAdditionalSaveData(CompoundTag compound) {
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

	public void readAdditionalSaveData(CompoundTag compound) {
		super.readAdditionalSaveData(compound);
		if (compound.contains("Texture")) {
			this.setTexture(compound.getString("Texture"));
		}

		if (compound.contains("DataChasing")) {
			this.entityData.set(DATA_Chasing, compound.getBoolean("DataChasing"));
		}

		if (compound.contains("DataIsBeingSeen")) {
			this.entityData.set(DATA_IsBeingSeen, compound.getBoolean("DataIsBeingSeen"));
		}

		if (compound.contains("DataIsBeingLookedAt")) {
			this.entityData.set(DATA_IsBeingLookedAt, compound.getBoolean("DataIsBeingLookedAt"));
		}

		if (compound.contains("DataCanSeePlayer")) {
			this.entityData.set(DATA_CanSeePlayer, compound.getBoolean("DataCanSeePlayer"));
		}

		if (compound.contains("DataAttacking")) {
			this.entityData.set(DATA_Attacking, compound.getInt("DataAttacking"));
		}

		if (compound.contains("DataAI")) {
			this.entityData.set(DATA_AI, compound.getInt("DataAI"));
		}

		if (compound.contains("DataDespawnOverTimeTimer")) {
			this.entityData.set(DATA_DespawnOverTimeTimer, compound.getInt("DataDespawnOverTimeTimer"));
		}

		if (compound.contains("DataChaseTimer")) {
			this.entityData.set(DATA_ChaseTimer, compound.getInt("DataChaseTimer"));
		}

		if (compound.contains("DataClimbing")) {
			this.entityData.set(DATA_Climbing, compound.getBoolean("DataClimbing"));
		}

		if (compound.contains("DataAmbientTimer")) {
			this.entityData.set(DATA_AmbientTimer, compound.getInt("DataAmbientTimer"));
		}

		if (compound.contains("DataWalkTarget")) {
			this.entityData.set(DATA_WalkTarget, compound.getString("DataWalkTarget"));
		}

		if (compound.contains("DataChaseHurtTarget")) {
			this.entityData.set(DATA_ChaseHurtTarget, compound.getString("DataChaseHurtTarget"));
		}

		if (compound.contains("DataChaseHurtTargetTimer")) {
			this.entityData.set(DATA_ChaseHurtTargetTimer, compound.getInt("DataChaseHurtTargetTimer"));
		}

		if (compound.contains("DataBreakDoorTimer")) {
			this.entityData.set(DATA_BreakDoorTimer, compound.getInt("DataBreakDoorTimer"));
		}

		if (compound.contains("DataBreakDoorAmount")) {
			this.entityData.set(DATA_BreakDoorAmount, compound.getInt("DataBreakDoorAmount"));
		}

		if (compound.contains("DataManifestTimer")) {
			this.entityData.set(DATA_ManifestTimer, compound.getInt("DataManifestTimer"));
		}

		if (compound.contains("DataDeManifestTimer")) {
			this.entityData.set(DATA_DeManifestTimer, compound.getInt("DataDeManifestTimer"));
		}

		if (compound.contains("DataPlayerNorth")) {
			this.entityData.set(DATA_PlayerNorth, compound.getBoolean("DataPlayerNorth"));
		}

		if (compound.contains("DataPlayerSouth")) {
			this.entityData.set(DATA_PlayerSouth, compound.getBoolean("DataPlayerSouth"));
		}

		if (compound.contains("DataPlayerEast")) {
			this.entityData.set(DATA_PlayerEast, compound.getBoolean("DataPlayerEast"));
		}

		if (compound.contains("DataPlayerWest")) {
			this.entityData.set(DATA_PlayerWest, compound.getBoolean("DataPlayerWest"));
		}

		if (compound.contains("DataChaseSection")) {
			this.entityData.set(DATA_ChaseSection, compound.getInt("DataChaseSection"));
		}

		if (compound.contains("DataChaseAmountRandomizer")) {
			this.entityData.set(DATA_ChaseAmountRandomizer, compound.getInt("DataChaseAmountRandomizer"));
		}

		if (compound.contains("DataRunToX")) {
			this.entityData.set(DATA_RunToX, compound.getInt("DataRunToX"));
		}

		if (compound.contains("DataRunToZ")) {
			this.entityData.set(DATA_RunToZ, compound.getInt("DataRunToZ"));
		}

		if (compound.contains("DataLifeSpanTimer")) {
			this.entityData.set(DATA_LifeSpanTimer, compound.getInt("DataLifeSpanTimer"));
		}

		if (compound.contains("DataPlayEventAnimation")) {
			this.entityData.set(DATA_PlayEventAnimation, compound.getBoolean("DataPlayEventAnimation"));
		}

		if (compound.contains("DataRunningAnimation")) {
			this.entityData.set(DATA_RunningAnimation, compound.getString("DataRunningAnimation"));
		}

		if (compound.contains("DataWalkingAnimation")) {
			this.entityData.set(DATA_WalkingAnimation, compound.getString("DataWalkingAnimation"));
		}

		if (compound.contains("DataIdleAnimation")) {
			this.entityData.set(DATA_IdleAnimation, compound.getString("DataIdleAnimation"));
		}

		if (compound.contains("DataEventAnimation")) {
			this.entityData.set(DATA_EventAnimation, compound.getString("DataEventAnimation"));
		}

		if (compound.contains("DataHorizontalClimb")) {
			this.entityData.set(DATA_HorizontalClimb, compound.getInt("DataHorizontalClimb"));
		}

		if (compound.contains("DataTransparency")) {
			this.entityData.set(DATA_Transparency, compound.getInt("DataTransparency"));
		}

		if (compound.contains("DataHorizontalClimbing")) {
			this.entityData.set(DATA_HorizontalClimbing, compound.getBoolean("DataHorizontalClimbing"));
		}

		if (compound.contains("DataGrabbingPlayer")) {
			this.entityData.set(DATA_GrabbingPlayer, compound.getInt("DataGrabbingPlayer"));
		}

		if (compound.contains("DataSmackPlayerOffPillar")) {
			this.entityData.set(DATA_SmackPlayerOffPillar, compound.getInt("DataSmackPlayerOffPillar"));
		}

		if (compound.contains("DataGrabPlayerCooldown")) {
			this.entityData.set(DATA_GrabPlayerCooldown, compound.getInt("DataGrabPlayerCooldown"));
		}

		if (compound.contains("DataDeManifestType")) {
			this.entityData.set(DATA_DeManifestType, compound.getInt("DataDeManifestType"));
		}

		if (compound.contains("DataChaseAmbientTimer")) {
			this.entityData.set(DATA_ChaseAmbientTimer, compound.getInt("DataChaseAmbientTimer"));
		}

		if (compound.contains("DataConcealSelf")) {
			this.entityData.set(DATA_ConcealSelf, compound.getInt("DataConcealSelf"));
		}

		if (compound.contains("DataConcealSelfTimer")) {
			this.entityData.set(DATA_ConcealSelfTimer, compound.getInt("DataConcealSelfTimer"));
		}

		if (compound.contains("DataLookedAtEventRandomizer")) {
			this.entityData.set(DATA_LookedAtEventRandomizer, compound.getInt("DataLookedAtEventRandomizer"));
		}

		if (compound.contains("DataRandomLookEventCooldown")) {
			this.entityData.set(DATA_RandomLookEventCooldown, compound.getInt("DataRandomLookEventCooldown"));
		}

		if (compound.contains("DataRunAtRunAwayEvent")) {
			this.entityData.set(DATA_RunAtRunAwayEvent, compound.getInt("DataRunAtRunAwayEvent"));
		}

		if (compound.contains("DataAIIdleTimer")) {
			this.entityData.set(DATA_AIIdleTimer, compound.getInt("DataAIIdleTimer"));
		}

		if (compound.contains("DataEnraged")) {
			this.entityData.set(DATA_Enraged, compound.getBoolean("DataEnraged"));
		}

		if (compound.contains("DataAggroStared")) {
			this.entityData.set(DATA_AggroStared, compound.getInt("DataAggroStared"));
		}

		if (compound.contains("DataFoldInArms")) {
			this.entityData.set(DATA_FoldInArms, compound.getBoolean("DataFoldInArms"));
		}

		if (compound.contains("DataGhostTarget")) {
			this.entityData.set(DATA_GhostTarget, compound.getString("DataGhostTarget"));
		}
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

	private PlayState movementPredicate(AnimationState event) {
		if (this.animationprocedure.equals("empty")) {
			if ((Boolean)this.entityData.get(DATA_PlayEventAnimation)) {
				return event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_EventAnimation)));
			} else if ((event.isMoving() || !(event.getLimbSwingAmount() > -0.15F) || !(event.getLimbSwingAmount() < 0.15F))
				&& !this.isAggressive()
				&& !this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_WalkingAnimation)));
			} else if (this.isInWaterOrBubble()) {
				return event.setAndContinue(RawAnimation.begin().thenLoop("animation.growling_man.swim"));
			} else {
				return this.isAggressive() && event.isMoving() && !this.isInWaterOrBubble()
					? event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_RunningAnimation)))
					: event.setAndContinue(RawAnimation.begin().thenLoop((String)this.entityData.get(DATA_IdleAnimation)));
			}
		} else {
			return PlayState.STOP;
		}
	}

	private PlayState attackingPredicate(AnimationState event) {
		double d1 = this.getX() - this.xOld;
		double d0 = this.getZ() - this.zOld;
		float velocity = (float)Math.sqrt(d1 * d1 + d0 * d0);
		if (this.getAttackAnim(event.getPartialTick()) > 0.0F && !this.swinging) {
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
			&& event.getController().getAnimationState() == State.STOPPED) {
			event.getController().forceAnimationReset();
			return event.setAndContinue(RawAnimation.begin().thenPlay("animation.growling_man.attack"));
		} else {
			return PlayState.CONTINUE;
		}
	}

	private PlayState procedurePredicate(AnimationState event) {
		if (!this.animationprocedure.equals("empty") && event.getController().getAnimationState() == State.STOPPED) {
			event.getController().setAnimation(RawAnimation.begin().thenPlay(this.animationprocedure));
			if (event.getController().getAnimationState() == State.STOPPED) {
				this.animationprocedure = "empty";
				event.getController().forceAnimationReset();
			}
		} else if (this.animationprocedure.equals("empty")) {
			return PlayState.STOP;
		}

		return PlayState.CONTINUE;
	}

	protected void tickDeath() {
		this.deathTime++;
		if (this.deathTime == 20) {
			this.remove(RemovalReason.KILLED);
			this.dropExperience(null);
		}
	}

	public String getSyncedAnimation() {
		return (String)this.entityData.get(ANIMATION);
	}

	public void setAnimation(String animation) {
		this.entityData.set(ANIMATION, animation);
	}

	public void registerControllers(ControllerRegistrar data) {
		data.add(new AnimationController[]{new AnimationController(this, "movement", 4, this::movementPredicate)});
		data.add(new AnimationController[]{new AnimationController(this, "attacking", 4, this::attackingPredicate)});
		data.add(new AnimationController[]{new AnimationController(this, "procedure", 4, this::procedurePredicate)});
	}

	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}
}
