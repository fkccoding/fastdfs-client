/*
Navicat MySQL Data Transfer

Source Server         : MySQL
Source Server Version : 50643
Source Host           : 192.100.2.36:3306
Source Database       : fastdfs

Target Server Type    : MYSQL
Target Server Version : 50643
File Encoding         : 65001

Date: 2019-04-22 19:26:24
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for file_info
-- ----------------------------
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `file_name` varchar(255) NOT NULL,
  `group_name` varchar(255) DEFAULT NULL,
  `remote_file_name` varchar(255) DEFAULT NULL,
  `upload_date` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `file_size` varchar(255) DEFAULT NULL,
  `real_size` bigint(255) DEFAULT NULL,
  `version` double(255,1) DEFAULT '1.0',
  `operator` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of file_info
-- ----------------------------
INSERT INTO `file_info` VALUES ('Cloud Netflix组件介绍及简单实例.docx', 'group2', 'M00/00/00/wGQCI1ysU6qAcjsZAC9Y1eP3d6Y09.docx', '2019-04-18 09:43:47', '2.96MB', '3103784', '1.0', '方程');
INSERT INTO `file_info` VALUES ('新一代电力交易平台（省间）-详细设计说明书-XX分册（模板）.docx', 'group2', 'M00/00/00/wGQCI1y1O6OAQTTbAAGas50jJoY52.docx', '2019-04-18 09:44:05', '102.67KB', '105134', '1.0', '方程');
INSERT INTO `file_info` VALUES ('新一代电力交易平台（省间）-详细设计说明书-基础支撑分册.docx', 'group2', 'M00/00/00/wGQCI1y3BLaAQHCrAAf1EukEqf431.docx', '2019-04-18 09:44:20', '509.27KB', '521492', '1.0', '曲广昊');
INSERT INTO `file_info` VALUES ('成果.rar', 'group2', 'M00/00/00/wGQCI1y3BNKAcdOgABcZR7mloig742.rar', '2019-04-18 09:44:50', '1.44MB', '1509949', '1.0', '向凌吉');
INSERT INTO `file_info` VALUES ('下载.jpg', 'group2', 'M00/00/00/wGQCI1y3BP2AR5caAABKEdlHJU0777.jpg', '2019-04-18 09:45:30', '18.52KB', '18964', '1.0', '江天');
INSERT INTO `file_info` VALUES ('images.jpg', 'group2', 'M00/00/00/wGQCI1y3BRGAZaOcAAAfW0LhGhk093.jpg', '2019-04-18 09:45:50', '7.84KB', '8028', '1.0', '卢荟芳');
INSERT INTO `file_info` VALUES ('images (3).jpg', 'group2', 'M00/00/00/wGQCI1y3BRmAceRXAAAhLQF0j2I767.jpg', '2019-04-18 09:45:58', '8.29KB', '8221', '1.0', '崔志臣');
INSERT INTO `file_info` VALUES ('images (4).jpg', 'group2', 'M00/00/00/wGQCI1y3BR-AI0SBAAAQgAyc_rA464.jpg', '2019-04-18 09:46:05', '4.13KB', '4201', '1.0', '魏健东');
INSERT INTO `file_info` VALUES ('star-line.svg', 'group2', 'M00/00/00/wGQCI1y9Zk6AYFyTAAACnpBRGJ4504.svg', '2019-04-22 14:59:24', '670.0B', '670', '1.0', '方程');
INSERT INTO `file_info` VALUES ('left-arrows.svg', 'group2', 'M00/00/00/wGQCI1y9ZlSAN9MVAAAA9gQD5e8250.svg', '2019-04-22 14:59:30', '246.0B', '246', '1.0', '方程');
INSERT INTO `file_info` VALUES ('qq音乐.jpeg', 'group2', 'M00/00/00/wGQCI1y9ZluAGiwtAAA4WXLMn6E49.jpeg', '2019-04-22 14:59:37', '14.09KB', '14425', '1.0', '方程');
INSERT INTO `file_info` VALUES ('data.js', 'group2', 'M00/00/00/wGQCI1y9a2mAeuilAAAK9Ttudsw7719.js', '2019-04-22 15:21:11', '2.74KB', '2805', '1.0', '方程');
INSERT INTO `file_info` VALUES ('1.vsdx', 'group2', 'M00/00/00/wGQCI1y9a_SAONyCAAZrhuXSV3828.vsdx', '2019-04-22 15:23:30', '410.88KB', '420742', '1.0', '方程');
INSERT INTO `file_info` VALUES ('（市场结算）新一代电力交易平台建设工作汇报V1.0.0-接口部分.pptx', 'group2', 'M00/00/00/wGQCI1y9bF6Ae2CRABfjuwMqPyE84.pptx', '2019-04-22 15:25:16', '1.49MB', '1565627', '1.0', '方程');
INSERT INTO `file_info` VALUES ('main.jpg', 'group2', 'M00/00/00/wGQCI1y9bKOAG6NmABPVLF_d1f8096.jpg', '2019-04-22 15:26:25', '1.24MB', '1299756', '1.0', '方程');
INSERT INTO `file_info` VALUES ('ShadowsocksX-NG.app.zip', 'group2', 'M00/00/00/wGQCI1y9bMKAJnrlAJe7yJ7I19c823.zip', '2019-04-22 15:26:55', '9.48MB', '9944008', '1.0', '方程');
INSERT INTO `file_info` VALUES ('qq音乐 (1).jpeg', 'group1', 'M00/00/00/wGQCCVy9bRSAY-wQAAA4WXLMn6E34.jpeg', '2019-04-22 15:27:33', '14.09KB', '14425', '1.0', '方程');
INSERT INTO `file_info` VALUES ('nasa-53884-unsplash.jpg', 'group1', 'M00/00/00/wGQCCVy9bWGAe5BbABCsr-RHpYI479.jpg', '2019-04-22 15:28:50', '1.04MB', '1092783', '1.0', '方程');
INSERT INTO `file_info` VALUES ('LHJS主页1.jpg', 'group1', 'M00/00/00/wGQCCVy9bWWAGZmGAATM-UPKH8k924.jpg', '2019-04-22 15:28:54', '307.24KB', '314617', '1.0', '方程');
INSERT INTO `file_info` VALUES ('BlackMarble20161km.jpg', 'group1', 'M00/00/00/wGQCCVy9bYmAe4OoEKZnYwV-YG8598.jpg', '2019-04-22 15:29:32', '266.4MB', '279340899', '1.0', '方程');
INSERT INTO `file_info` VALUES ('2019清明小长假出行预测报告.pdf', 'group2', 'M00/00/00/wGQCI1y9buOAM6IGAB8gt68Ihmk514.pdf', '2019-04-22 15:36:00', '1.95MB', '2039991', '1.0', '方程');
INSERT INTO `file_info` VALUES ('第一讲：全面认识新时代历史征程.pptx', 'group2', 'M00/00/00/wGQCI1y9dj2ACMk3AeYcDOmQrc054.pptx', '2019-04-22 16:07:22', '30.38MB', '31857676', '1.0', '方程');
INSERT INTO `file_info` VALUES ('task_4245.tar_19.gz', 'group2', 'M00/00/00/wGQCI1y9dp6AM7E6AABr1lmWqSk7256.gz', '2019-04-22 16:08:30', '26.96KB', '27606', '1.0', '王旭伟');
INSERT INTO `file_info` VALUES ('OpenStack简单介绍.docx', 'group2', 'M00/00/00/wGQCI1y9dsiAJdaBAABGoUQOXvs63.docx', '2019-04-22 16:09:12', '17.66KB', '18081', '1.0', '高洪伟');
INSERT INTO `file_info` VALUES ('秒速五厘米.mp4', 'group2', 'M00/00/00/wGQCI1y9d9KAY6dMAFe0-nUxW5I105.mp4', '2019-04-22 16:14:08', '5.48MB', '5747962', '1.0', '海军');
INSERT INTO `file_info` VALUES ('沙漠骆驼.MP3', 'group2', 'M00/00/00/wGQCI1y9d-aAGfF1AFKLmS-gq6s603.MP3', '2019-04-22 16:14:27', '5.16MB', '5409689', '1.0', '王旭伟');
INSERT INTO `file_info` VALUES ('天王星.jpg', 'group2', 'M00/00/00/wGQCI1y9eXmAAQD6AAEpEYTKTY8182.jpg', '2019-04-22 16:21:10', '74.27KB', '76049', '1.0', '高洪伟');
INSERT INTO `file_info` VALUES ('鸟.jpg', 'group2', 'M00/00/00/wGQCI1y9ee6AXhGAAAKJTj9gYaM832.jpg', '2019-04-22 16:23:07', '162.33KB', '166222', '1.0', '江天');
INSERT INTO `file_info` VALUES ('(03) [周杰倫] 星晴.flac', 'group2', 'M00/00/00/wGQCI1y9fRmAdUc4Aa9whmR4Ymc65.flac', '2019-04-22 16:36:39', '26.96MB', '28274822', '1.0', '栾希鹏');
INSERT INTO `file_info` VALUES ('(11) [周杰倫] 稻香.flac', 'group2', 'M00/00/00/wGQCI1y9fVWAFIm4AZG3tb5rg8w87.flac', '2019-04-22 16:37:38', '25.11MB', '26326965', '1.0', '卢荟芳');
INSERT INTO `file_info` VALUES ('04.异乡人（2013版）.mp3', 'group2', 'M00/00/00/wGQCI1y9fw-ASVnVAIkkY9wqBpY721.mp3', '2019-04-22 16:45:01', '8.57MB', '8987747', '1.0', '梅峥');
INSERT INTO `file_info` VALUES ('Michael Jackson - We Are The World (Demo).mp3', 'group2', 'M00/00/00/wGQCI1y9f-WAY2xdAHYZ8jju9II914.mp3', '2019-04-22 16:48:34', '7.38MB', '7739890', '1.0', '张飞');
