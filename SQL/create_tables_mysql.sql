--
-- create tables for MySQL.
-- author kageyama
-- date 2025/05/09
--

-- drop existing tables if necessary.
DROP TABLE IF EXISTS dmarc_auth_results;
DROP TABLE IF EXISTS dmarc_records;
DROP TABLE IF EXISTS dmarc_feedbacks;

--
-- feedback records.
CREATE TABLE dmarc_feedbacks (
	org_name VARCHAR(128) NOT NULL,
	report_id VARCHAR(64) NOT NULL,
	email VARCHAR(128) DEFAULT NULL,
	extra_contact_info VARCHAR(256) DEFAULT NULL,
	begin INT NOT NULL,
	end INT NOT NULL,
	domain VARCHAR(128) NOT NULL,
	adkim VARCHAR(8) NOT NULL,
	aspf VARCHAR(8) NOT NULL,
	p VARCHAR(128) DEFAULT NULL,
	sp VARCHAR(128) DEFAULT NULL,
	pct INT NOT NULL DEFAULT 0,
	np VARCHAR(128) DEFAULT NULL,

	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

	PRIMARY KEY (org_name, report_id)
) ENGINE=InnoDB, CHARACTER SET utf8mb4, COLLATE 'utf8mb4_bin';

--
-- record records.
CREATE TABLE dmarc_records (
	org_name VARCHAR(128) NOT NULL,
	report_id VARCHAR(64) NOT NULL,
	source_ip VARCHAR(128) NOT NULL,
	count INT NOT NULL,
	disposition VARCHAR(12) DEFAULT NULL,
	dkim VARCHAR(12) DEFAULT NULL,
	spf VARCHAR(12) DEFAULT NULL,
	header_from VARCHAR(128) NOT NULL,

	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

	PRIMARY KEY (org_name, report_id, source_ip),
	FOREIGN KEY (org_name, report_id) REFERENCES dmarc_feedbacks (org_name, report_id)
) ENGINE=InnoDB, CHARACTER SET utf8mb4, COLLATE 'utf8mb4_bin';

--
-- record dkim records.
CREATE TABLE dmarc_auth_results (
	org_name VARCHAR(128) NOT NULL,
	report_id VARCHAR(64) NOT NULL,
	source_ip VARCHAR(128) NOT NULL,
	type VARCHAR(8) NOT NULL,	-- 'dkim' or 'spf'
	row INT NOT NULL,
	domain VARCHAR(128) DEFAULT NULL,
	selector VARCHAR(128) DEFAULT NULL,
	result VARCHAR(12) NOT NULL,

	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

	PRIMARY KEY (org_name, report_id, source_ip, type, row),
	FOREIGN KEY (org_name, report_id, source_ip) REFERENCES dmarc_records (org_name, report_id, source_ip)
) ENGINE=InnoDB, CHARACTER SET utf8mb4, COLLATE 'utf8mb4_bin';
