-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema cars
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cars
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cars` DEFAULT CHARACTER SET utf8 ;
USE `cars` ;

-- -----------------------------------------------------
-- Table `cars`.`Owner`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cars`.`Owner` (
                                              `ID` INT NOT NULL AUTO_INCREMENT,
                                              `AHV_Number` VARCHAR(45) NOT NULL,
    PRIMARY KEY (`ID`),
    UNIQUE INDEX `AHV_Number_UNIQUE` (`AHV_Number` ASC) VISIBLE)
    ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cars`.`Car`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `cars`.`Car` (
                                            `ID` INT NOT NULL AUTO_INCREMENT,
                                            `Owner_ID` INT NOT NULL,
                                            `Make` VARCHAR(45) NOT NULL,
    `Model` VARCHAR(45) NOT NULL,
    `Production_Date` DATE NOT NULL,
    `Approved` TINYINT NOT NULL,
    `Price` DECIMAL(10,2) NOT NULL,
    PRIMARY KEY (`ID`),
    INDEX `fk_Car_Owner_idx` (`Owner_ID` ASC) VISIBLE,
    CONSTRAINT `fk_Car_Owner`
    FOREIGN KEY (`Owner_ID`)
    REFERENCES `cars`.`Owner` (`ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
    ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
