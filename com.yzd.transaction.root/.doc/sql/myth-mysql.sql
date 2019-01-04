/*
Navicat MySQL Data Transfer

Source Server         : 192.168.1.234-myth
Source Server Version : 50724
Source Host           : 192.168.1.234:3306
Source Database       : myth_account

Target Server Type    : MYSQL
Target Server Version : 50724
File Encoding         : 65001

Date: 2019-01-04 17:17:15
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for tb_account
-- ----------------------------
DROP TABLE IF EXISTS `tb_account`;
CREATE TABLE `tb_account` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(128) COLLATE utf8mb4_bin NOT NULL,
  `balance` decimal(10,0) NOT NULL COMMENT '用户余额',
  `create_time` datetime NOT NULL,
  `update_time` datetime DEFAULT NULL,
  `gmt_timestamp` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;

-- ----------------------------
-- Table structure for tb_transaction_activity
-- ----------------------------
DROP TABLE IF EXISTS `tb_transaction_activity`;
CREATE TABLE `tb_transaction_activity` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `txc_id` bigint(20) DEFAULT NULL COMMENT '全局事务id',
  `txc_type_value` int(11) DEFAULT NULL COMMENT '事务类型',
  `txc_type_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事务类型名称',
  `txc_detail_jaon` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事务详情',
  `txc_state` int(11) DEFAULT NULL COMMENT '事务状态',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='主事务记录';

-- ----------------------------
-- Table structure for tb_transaction_activity_detail
-- ----------------------------
DROP TABLE IF EXISTS `tb_transaction_activity_detail`;
CREATE TABLE `tb_transaction_activity_detail` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `txc_id` bigint(20) DEFAULT NULL COMMENT '全局事务id',
  `txc_brance_id` varchar(100) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '分支事务id',
  `txc_type_value` int(11) DEFAULT NULL COMMENT '事务类型',
  `txc_type_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事务类型名称',
  `txc_detail_jaon` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事务详情',
  `txc_state` int(11) DEFAULT NULL COMMENT '事务状态',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin COMMENT='主事务记录详情';

-- ----------------------------
-- Table structure for tb_txc_message
-- ----------------------------
DROP TABLE IF EXISTS `tb_txc_message`;
CREATE TABLE `tb_txc_message` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `txc_id` bigint(20) DEFAULT NULL COMMENT '事物id',
  `txc_trace_id` bigint(20) DEFAULT NULL COMMENT '事物跟踪id',
  `txc_type_value` int(11) DEFAULT NULL COMMENT '事务类别',
  `txc_type_name` varchar(50) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事务类别名称',
  `message_json` varchar(1000) COLLATE utf8mb4_bin DEFAULT NULL COMMENT '事务日志',
  `gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;
