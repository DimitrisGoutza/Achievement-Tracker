DROP SCHEMA IF EXISTS `achievement_tracker`;

CREATE SCHEMA `achievement_tracker`;

use `achievement_tracker`;

CREATE TABLE `game_detail` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `short_description` VARCHAR(400) NOT NULL,
    `long_description` TEXT NOT NULL,
    `header_image` VARCHAR(100) NOT NULL,
    `capsule_image` VARCHAR(100) NOT NULL,
    `capsule_small_image` VARCHAR(100) NOT NULL,
    `background_image` VARCHAR(100) NOT NULL,
    `background_raw_image` VARCHAR(100) NOT NULL,
    `total_achievements` INT NOT NULL,
    PRIMARY KEY (`id`));

CREATE TABLE `game` (
    `id` INT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `release_date` DATE NULL,
    `coming_soon` TINYINT(1) NOT NULL,
    `score` DECIMAL(5,2) NOT NULL,
    `game_detail_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK_GAME_DETAIL_idx` (`game_detail_id` ASC) VISIBLE,
    CONSTRAINT `FK_GAME_DETAIL` FOREIGN KEY (`game_detail_id`)
        REFERENCES `game_detail` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE);

CREATE TABLE `achievement` (
    `id` INT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(125) NOT NULL,
    `display_name` VARCHAR(400) NOT NULL,
    `description` TEXT NULL,
    `hidden` TINYINT(1) NOT NULL,
    `percentage` DECIMAL(5,2) NOT NULL,
    `icon` VARCHAR(125) NOT NULL,
    `icon_gray` VARCHAR(125) NOT NULL,
    `game_id` INT NOT NULL,
    PRIMARY KEY (`id`),
    INDEX `FK_GAME_idx` (`game_id` ASC) VISIBLE,
    CONSTRAINT `FK_GAME` FOREIGN KEY (`game_id`)
        REFERENCES `game` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE);