package net.voxla.growlingman.procedures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.LevelAccessor;
import net.voxla.growlingman.entity.GrowlingManEntity;

public class ConcealBehindBlocksProcedure {
   public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
      if (entity != null) {
         double sx = 0.0;
         double sy = 0.0;
         double sz = 0.0;
         boolean canhide = false;
         if (entity instanceof GrowlingManEntity _datEntL0 && (Boolean)_datEntL0.getEntityData().get(GrowlingManEntity.DATA_PlayerEast)) {
            canhide = false;
            sx = -32.0;

            for (int index0 = 0; index0 < 64; index0++) {
               sy = 0.0;

               for (int index1 = 0; index1 < 2; index1++) {
                  sz = -32.0;

                  for (int index2 = 0; index2 < 64; index2++) {
                     if (world.getBlockState(BlockPos.containing(x + sx + 1.0, y + sy, z + sz)).canOcclude()
                        && world.getBlockState(BlockPos.containing(x + sx + 1.0, y + sy + 1.0, z + sz)).canOcclude()
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy, z + sz))
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy + 1.0, z + sz))) {
                        if (entity instanceof Mob _entity) {
                           _entity.getNavigation().moveTo(x + sx, y + sy, z + sz, 2.0);
                        }

                        if (entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ConcealSelf, 0);
                        }

                        if ((
                                 entity instanceof GrowlingManEntity _datEntI
                                    ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
                                    : 0
                              )
                              != 0
                           && entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, 0);
                        }

                        canhide = true;
                        break;
                     }

                     sz++;
                  }

                  sy++;
               }

               sx++;
            }

            if (!canhide
               && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }
         } else if (entity instanceof GrowlingManEntity _datEntL11 && (Boolean)_datEntL11.getEntityData().get(GrowlingManEntity.DATA_PlayerWest)) {
            canhide = false;
            sx = -32.0;

            for (int index3 = 0; index3 < 64; index3++) {
               sy = 0.0;

               for (int index4 = 0; index4 < 2; index4++) {
                  sz = -32.0;

                  for (int index5 = 0; index5 < 64; index5++) {
                     if (world.getBlockState(BlockPos.containing(x + sx - 1.0, y + sy, z + sz)).canOcclude()
                        && world.getBlockState(BlockPos.containing(x + sx - 1.0, y + sy + 1.0, z + sz)).canOcclude()
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy, z + sz))
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy + 1.0, z + sz))) {
                        if (entity instanceof Mob _entity) {
                           _entity.getNavigation().moveTo(x + sx, y + sy, z + sz, 2.0);
                        }

                        if (entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ConcealSelf, 0);
                        }

                        if ((
                                 entity instanceof GrowlingManEntity _datEntI
                                    ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
                                    : 0
                              )
                              != 0
                           && entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, 0);
                        }

                        canhide = true;
                        break;
                     }

                     sz++;
                  }

                  sy++;
               }

               sx++;
            }

            if (!canhide
               && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }
         } else if (entity instanceof GrowlingManEntity _datEntL22 && (Boolean)_datEntL22.getEntityData().get(GrowlingManEntity.DATA_PlayerSouth)) {
            canhide = false;
            sx = -32.0;

            for (int index6 = 0; index6 < 64; index6++) {
               sy = 0.0;

               for (int index7 = 0; index7 < 2; index7++) {
                  sz = -32.0;

                  for (int index8 = 0; index8 < 64; index8++) {
                     if (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz + 1.0)).canOcclude()
                        && world.getBlockState(BlockPos.containing(x + sx, y + sy + 1.0, z + sz + 1.0)).canOcclude()
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy, z + sz))
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy + 1.0, z + sz))) {
                        if (entity instanceof Mob _entity) {
                           _entity.getNavigation().moveTo(x + sx, y + sy, z + sz, 2.0);
                        }

                        if (entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ConcealSelf, 0);
                        }

                        if ((
                                 entity instanceof GrowlingManEntity _datEntI
                                    ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
                                    : 0
                              )
                              != 0
                           && entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, 0);
                        }

                        canhide = true;
                        break;
                     }

                     sz++;
                  }

                  sy++;
               }

               sx++;
            }

            if (!canhide
               && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }
         } else if (entity instanceof GrowlingManEntity _datEntL33 && (Boolean)_datEntL33.getEntityData().get(GrowlingManEntity.DATA_PlayerNorth)) {
            canhide = false;
            sx = -32.0;

            for (int index9 = 0; index9 < 64; index9++) {
               sy = 0.0;

               for (int index10 = 0; index10 < 2; index10++) {
                  sz = -32.0;

                  for (int index11 = 0; index11 < 64; index11++) {
                     if (world.getBlockState(BlockPos.containing(x + sx, y + sy, z + sz - 1.0)).canOcclude()
                        && world.getBlockState(BlockPos.containing(x + sx, y + sy + 1.0, z + sz - 1.0)).canOcclude()
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy, z + sz))
                        && world.isEmptyBlock(BlockPos.containing(x + sx, y + sy + 1.0, z + sz))) {
                        if (entity instanceof Mob _entity) {
                           _entity.getNavigation().moveTo(x + sx, y + sy, z + sz, 2.0);
                        }

                        if (entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_ConcealSelf, 0);
                        }

                        if ((
                                 entity instanceof GrowlingManEntity _datEntI
                                    ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_LookedAtEventRandomizer)
                                    : 0
                              )
                              != 0
                           && entity instanceof GrowlingManEntity _datEntSetI) {
                           _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_LookedAtEventRandomizer, 0);
                        }

                        canhide = true;
                        break;
                     }

                     sz++;
                  }

                  sy++;
               }

               sx++;
            }

            if (!canhide
               && (entity instanceof GrowlingManEntity _datEntI ? (Integer)_datEntI.getEntityData().get(GrowlingManEntity.DATA_DeManifestTimer) : 0) == 0
               && entity instanceof GrowlingManEntity _datEntSetI) {
               _datEntSetI.getEntityData().set(GrowlingManEntity.DATA_DeManifestTimer, 1);
            }
         }
      }
   }
}
