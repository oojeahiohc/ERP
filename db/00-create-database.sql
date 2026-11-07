-- 개발 DB와 한국 표준시 설정
CREATE DATABASE IF NOT EXISTS erp_dev
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE erp_dev;
SET time_zone = '+09:00';