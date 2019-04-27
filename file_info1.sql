/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50643
Source Host           : 192.100.2.36:3306
Source Database       : fastdfs

Target Server Type    : MYSQL
Target Server Version : 50643
File Encoding         : 65001

Date: 2019-04-26 22:13:08
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file_info1
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `file_name` varchar(100) NOT NULL,
  `group_name` varchar(10) DEFAULT NULL,
  `remote_file_name` varchar(100) DEFAULT NULL,
  `upload_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `file_size` varchar(100) DEFAULT NULL,
  `real_size` bigint(20) DEFAULT NULL,
  `version` double(2,1) DEFAULT '1.0',
  `operator` varchar(10) DEFAULT NULL,
  `is_new` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
