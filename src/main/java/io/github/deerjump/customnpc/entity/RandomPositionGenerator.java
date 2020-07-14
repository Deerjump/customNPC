package io.github.deerjump.customnpc.entity;

import java.util.Random;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import javax.annotation.Nullable;

import net.minecraft.server.v1_16_R1.BlockPosition;
import net.minecraft.server.v1_16_R1.MathHelper;
import net.minecraft.server.v1_16_R1.NavigationAbstract;
import net.minecraft.server.v1_16_R1.PathType;
import net.minecraft.server.v1_16_R1.PathfinderNormal;
import net.minecraft.server.v1_16_R1.TagsFluid;
import net.minecraft.server.v1_16_R1.Vec3D;

public class RandomPositionGenerator {

   @Nullable
   public static Vec3D a(EntityAbstract var0, int var1, int var2) {
       return a(var0, var1, var2, 0, (Vec3D)null, true, 1.5707963705062866D, var0::f, false, 0, 0, true);
   }

   @Nullable
   public static Vec3D a(EntityAbstract var0, int var1, int var2, int var3, @Nullable Vec3D var4, double var5) {
       return a(var0, var1, var2, var3, var4, true, var5, null, true, 0, 0, false);
   }

   @Nullable
   public static Vec3D b(EntityAbstract var0, int var1, int var2) {
       var0.getClass();
       return a(var0, var1, var2, (ToDoubleFunction<BlockPosition>)null);
   }

   @Nullable
   public static Vec3D a(EntityAbstract var0, int var1, int var2, ToDoubleFunction<BlockPosition> var3) {
       return a(var0, var1, var2, 0, (Vec3D)null, false, 0.0D, var3, true, 0, 0, true);
   }

   @Nullable
   public static Vec3D a(EntityAbstract var0, int var1, int var2, Vec3D var3, float var4, int var5, int var6) {
       return a(var0, var1, var2, 0, var3, false, (double)var4, null, true, var5, var6, true);
   }

   @Nullable
   public static Vec3D a(EntityAbstract var0, int var1, int var2, Vec3D var3) {
       Vec3D var4 = var3.a(var0.locX(), var0.locY(), var0.locZ());
       return a(var0, var1, var2, 0, var4, false, 1.5707963705062866D, null, true, 0, 0, true);
   }

   @Nullable
   public static Vec3D b(EntityAbstract var0, int var1, int var2, Vec3D var3) {
       Vec3D var4 = var3.a(var0.locX(), var0.locY(), var0.locZ());
       return a(var0, var1, var2, 0, var4, true, 1.5707963705062866D, null, false, 0, 0, true);
   }

   @Nullable
   public static Vec3D a(EntityAbstract var0, int var1, int var2, Vec3D var3, double var4) {
       Vec3D var6 = var3.a(var0.locX(), var0.locY(), var0.locZ());
       return a(var0, var1, var2, 0, var6, true, var4, null, false, 0, 0, true);
   }

   @Nullable
   public static Vec3D b(EntityAbstract var0, int var1, int var2, int var3, Vec3D var4, double var5) {
       Vec3D var7 = var4.a(var0.locX(), var0.locY(), var0.locZ());
       return a(var0, var1, var2, var3, var7, false, var5, null, true, 0, 0, false);
   }

   @Nullable
   public static Vec3D c(EntityAbstract var0, int var1, int var2, Vec3D var3) {
       Vec3D var4 = var0.getPositionVector().d(var3);
       return a(var0, var1, var2, 0, var4, true, 1.5707963705062866D, null, false, 0, 0, true);
   }

   @Nullable
   public static Vec3D d(EntityAbstract var0, int var1, int var2, Vec3D var3) {
       Vec3D var4 = var0.getPositionVector().d(var3);
       return a(var0, var1, var2, 0, var4, false, 1.5707963705062866D, null, true, 0, 0, true);
   }

   @Nullable
   private static Vec3D a(EntityAbstract var0, int var1, int var2, int var3, @Nullable Vec3D var4, boolean var5, double var6, ToDoubleFunction<BlockPosition> var8, boolean var9, int var10, int var11, boolean var12) {
       NavigationAbstract var13 = var0.getNavigation();
       Random var14 = var0.getRandom();
       boolean var15;
       if (var0.eA()) {
           var15 = var0.ex().a(var0.getPositionVector(), (double)(var0.ey() + (float)var1) + 1.0D);
       } else {
           var15 = false;
       }

       boolean var16 = false;
       double var17 = -1.0D / 0.0;
       BlockPosition var19 = var0.getChunkCoordinates();

       for(int var20 = 0; var20 < 10; ++var20) {
           BlockPosition var21 = a(var14, var1, var2, var3, var4, var6);
           if (var21 != null) {
               int var22 = var21.getX();
               int var23 = var21.getY();
               int var24 = var21.getZ();
               BlockPosition var25;
               if (var0.eA() && var1 > 1) {
                   var25 = var0.ex();
                   if (var0.locX() > (double)var25.getX()) {
                       var22 -= var14.nextInt(var1 / 2);
                   } else {
                       var22 += var14.nextInt(var1 / 2);
                   }

                   if (var0.locZ() > (double)var25.getZ()) {
                       var24 -= var14.nextInt(var1 / 2);
                   } else {
                       var24 += var14.nextInt(var1 / 2);
                   }
               }

               var25 = new BlockPosition((double)var22 + var0.locX(), (double)var23 + var0.locY(), (double)var24 + var0.locZ());
               if (var25.getY() >= 0 && var25.getY() <= var0.world.getBuildHeight() && (!var15 || var0.a(var25)) && (!var12 || var13.a(var25))) {
                   if (var9) {
                       var25 = a(var25, var14.nextInt(var10 + 1) + var11, var0.world.getBuildHeight(), (var1x) -> {
                           return var0.world.getType(var1x).getMaterial().isBuildable();
                       });
                   }

                   if (var5 || !var0.world.getFluid(var25).a(TagsFluid.WATER)) {
                       PathType var26 = PathfinderNormal.a(var0.world, var25.i());
                       if (var0.a(var26) == 0.0F) {
                           double var27 = var8.applyAsDouble(var25);
                           if (var27 > var17) {
                               var17 = var27;
                               var19 = var25;
                               var16 = true;
                           }
                       }
                   }
               }
           }
       }

       if (var16) {
           return Vec3D.c(var19);
       } else {
           return null;
       }
   }

   @Nullable
   private static BlockPosition a(Random var0, int var1, int var2, int var3, @Nullable Vec3D var4, double var5) {
       if (var4 != null && var5 < 3.141592653589793D) {
           double var7 = MathHelper.d(var4.z, var4.x) - 1.5707963705062866D;
           double var9 = var7 + (double)(2.0F * var0.nextFloat() - 1.0F) * var5;
           double var11 = Math.sqrt(var0.nextDouble()) * (double)MathHelper.a * (double)var1;
           double var13 = -var11 * Math.sin(var9);
           double var15 = var11 * Math.cos(var9);
           if (Math.abs(var13) <= (double)var1 && Math.abs(var15) <= (double)var1) {
               int var17 = var0.nextInt(2 * var2 + 1) - var2 + var3;
               return new BlockPosition(var13, (double)var17, var15);
           } else {
               return null;
           }
       } else {
           int var7 = var0.nextInt(2 * var1 + 1) - var1;
           int var8 = var0.nextInt(2 * var2 + 1) - var2 + var3;
           int var9 = var0.nextInt(2 * var1 + 1) - var1;
           return new BlockPosition(var7, var8, var9);
       }
   }

   static BlockPosition a(BlockPosition var0, int var1, int var2, Predicate<BlockPosition> var3) {
       if (var1 < 0) {
           throw new IllegalArgumentException("aboveSolidAmount was " + var1 + ", expected >= 0");
       } else if (!var3.test(var0)) {
           return var0;
       } else {
           BlockPosition var4;
           for(var4 = var0.up(); var4.getY() < var2 && var3.test(var4); var4 = var4.up()) {
           }

           BlockPosition var5;
           BlockPosition var6;
           for(var5 = var4; var5.getY() < var2 && var5.getY() - var4.getY() < var1; var5 = var6) {
               var6 = var5.up();
               if (var3.test(var6)) {
                   break;
               }
           }

           return var5;
       }
   }
}