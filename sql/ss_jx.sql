/*
 Navicat Premium Data Transfer

 Source Server         : self_coonect
 Source Server Type    : MySQL
 Source Server Version : 50651
 Source Host           : localhost:3306
 Source Schema         : ss_jx

 Target Server Type    : MySQL
 Target Server Version : 50651
 File Encoding         : 65001

 Date: 15/03/2023 16:51:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for ensure
-- ----------------------------
DROP TABLE IF EXISTS `ensure`;
CREATE TABLE `ensure`  (
  `ensure_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '状态确认表主键id',
  `year` int(4) NOT NULL COMMENT '年份',
  `month` int(2) NOT NULL COMMENT '月份',
  `user_name` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `tel` bigint(11) NOT NULL COMMENT '电话',
  `status` int(3) NOT NULL DEFAULT 100 COMMENT '确认状态：100：未发送、101：已发送、102：已查看、103：已确认',
  `evidence` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户确认签名的存储路径',
  `prove` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '用户月绩效存证生成后的保存路径',
  PRIMARY KEY (`ensure_id`) USING BTREE,
  INDEX `tel`(`tel`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '绩效状态表ensure；\r\n预期在签名认证阶段使用字段sign保存签名' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for jxcontent
-- ----------------------------
DROP TABLE IF EXISTS `jxcontent`;
CREATE TABLE `jxcontent`  (
  `jxcontent_id` int(5) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '绩效内容表主键id',
  `year` int(4) NOT NULL DEFAULT 9999 COMMENT '年份',
  `month` int(2) NOT NULL DEFAULT 99 COMMENT '月份',
  `tel` bigint(11) NOT NULL DEFAULT 99999 COMMENT '手机号',
  `user_name` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '姓名',
  `degree` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '完成度',
  `attitude` int(4) NULL DEFAULT NULL COMMENT '工作态度',
  `duty` int(4) NULL DEFAULT NULL COMMENT '管理责任',
  `pm_points` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '加分/扣分',
  `jx_points` float(6, 1) NULL DEFAULT NULL COMMENT '绩效得分',
  `coefficient` float(4, 1) NULL DEFAULT NULL COMMENT '绩效系数',
  `rowkey` int(4) NULL DEFAULT NULL COMMENT '当前行号',
  PRIMARY KEY (`jxcontent_id`) USING BTREE,
  INDEX `tel`(`tel`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 67 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '绩效内容表jxcontent\r\n面向员工展示所用' ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for ori_jxcontent
-- ----------------------------
DROP TABLE IF EXISTS `ori_jxcontent`;
CREATE TABLE `ori_jxcontent`  (
  `ori_jxcontent_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '绩效存根表主键',
  `year` int(4) NULL DEFAULT NULL COMMENT '年份',
  `month` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '月份',
  `ori_detail` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '绩效表原始内容',
  `rowkey` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '当前行号',
  `do_time` datetime(2) NOT NULL DEFAULT '0000-00-00 00:00:00.00' ON UPDATE CURRENT_TIMESTAMP(2) COMMENT '操作时间',
  PRIMARY KEY (`ori_jxcontent_id`, `do_time`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_name` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户名',
  `user_password` varchar(18) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户密码',
  `auth` int(1) NOT NULL COMMENT '用户权限：1、普通员工；2、管理员',
  `tel` bigint(11) NOT NULL COMMENT '电话',
  `department` varchar(16) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `tel`(`tel`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 48 CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户表user；\r\n包含用户信息及权限标识' ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
