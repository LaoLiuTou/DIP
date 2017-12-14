/*
MySQL Backup
Source Server Version: 5.7.20
Source Database: zytx
Date: 2017/12/12 16:41:17
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
--  Table structure for `sys_datasources`
-- ----------------------------
DROP TABLE IF EXISTS `sys_datasources`;
CREATE TABLE `sys_datasources` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `C_DT` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UP_DT` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `MEM_ID` int(11) DEFAULT NULL COMMENT '创建者id',
  `STATUS` varchar(40) DEFAULT NULL COMMENT '状态',
  `NM_T` varchar(500) DEFAULT NULL COMMENT '数据源名称',
  `DS_JSON` json DEFAULT NULL COMMENT '数据源信息',
  `PRO_ID` int(11) DEFAULT NULL COMMENT '项目id',
  PRIMARY KEY (`ID`),
  KEY `IDX_DAT_PRO_ID` (`PRO_ID`),
  KEY `IDX_DAT_MEM_ID` (`MEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sys_entities`
-- ----------------------------
DROP TABLE IF EXISTS `sys_entities`;
CREATE TABLE `sys_entities` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `C_DT` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UP_DT` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `MEM_ID` int(11) DEFAULT NULL COMMENT '创建者ID',
  `STATUS` varchar(40) DEFAULT NULL COMMENT '状态',
  `NM_T` varchar(1000) DEFAULT NULL COMMENT '实体名称',
  `DESC_T` varchar(1000) DEFAULT NULL COMMENT '描述',
  `CODE_T` varchar(1000) DEFAULT NULL COMMENT '代码',
  `DAT_ID` int(11) NOT NULL COMMENT '数据源ID',
  PRIMARY KEY (`ID`),
  KEY `IDX_ENT_DAT_ID` (`DAT_ID`),
  KEY `IDX_ENT_MEM_ID` (`MEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Table structure for `sys_members`
-- ----------------------------
DROP TABLE IF EXISTS `sys_members`;
CREATE TABLE `sys_members` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `C_DT` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `UP_DT` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  `MEM_ID` int(11) DEFAULT NULL COMMENT '创建者ID',
  `STATUS` varchar(40) DEFAULT NULL COMMENT '状态',
  `USERNAME` varchar(100) DEFAULT NULL COMMENT '用户名',
  `USERPWD` varchar(100) DEFAULT NULL COMMENT '密码',
  `DISPLAYNAME` varchar(100) DEFAULT NULL COMMENT '姓名',
  PRIMARY KEY (`ID`),
  KEY `IDX_MEM_MEM_ID` (`MEM_ID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;

-- ----------------------------
--  Records 
-- ----------------------------
INSERT INTO `sys_datasources` VALUES ('1','2017-11-27 13:51:41','2017-11-29 10:10:57','1','1','testds1','{\"dbHost\": \"127.0.0.1\", \"dbName\": \"test111\", \"dbPort\": \"3306\", \"dbType\": \"mysql\", \"dbUser\": \"root\", \"dbPassword\": \"root\"}','1'), ('2','2017-11-27 13:53:16','2017-12-12 13:59:38','1','1','testds2','{\"dbHost\": \"192.168.1.8\", \"dbName\": \"file\", \"dbPort\": \"3306\", \"dbType\": \"mysql\", \"dbUser\": \"root\", \"dbPassword\": \"\"}','1');
INSERT INTO `sys_entities` VALUES ('1','2017-11-27 15:38:25','2017-11-27 15:38:25','1','11','testentities','111111','111','1'), ('2','2017-11-29 14:24:10','2017-11-29 14:24:10','1','1','testst1','nothing','nothing','1');
INSERT INTO `sys_members` VALUES ('1','2017-11-27 13:52:46','2017-12-08 09:58:57','2','1','testuser','e10adc3949ba59abbe56e057f20f883e','测试用户'), ('2','2017-11-30 09:19:26','2017-12-08 09:58:53',NULL,'1','admin','670b14728ad9902aecba32e22fa4f6bd','超级管理员');
