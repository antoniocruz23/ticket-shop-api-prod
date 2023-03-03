-- 3 Countries
INSERT INTO countries (country_id, currency, iso_code2, iso_code3, language, name, phone_code)
VALUES (1, 'EUR', 'PT', 'PRT', 'PT', 'Portugal', '351');

INSERT INTO countries (country_id, currency, iso_code2, iso_code3, language, name, phone_code)
VALUES (2, 'GBP', 'UK', 'GBR', 'GB', 'United Kingdom', '44');

INSERT INTO countries (country_id, currency, iso_code2, iso_code3, language, name, phone_code)
VALUES (3, 'EUR', 'FR', 'FRA', 'FR', 'France', '33');

-- 7 Addresses
INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Porto', 'Rua Tripeiro', '4560', 1);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Coventry', 'Gosford street', 'Cv1341', 2);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Lisbon', 'Almada', '2000', 1);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Paris', 'Effiel Street', 'Fr2134', 3);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Gaia', '4 andar', '3452831', 1);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Matosinhos', 'Rotunda pescador', '22442', 1);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('London', 'Underground', 'Ln1234', 2);

INSERT INTO addresses (city, line1, post_code, country_id)
VALUES ('Musical', 'Underground street', 'Ln1234', 2);

-- 4 Companies
INSERT INTO companies (created_at, email, name, website, address_id)
VALUES (now(), 'coventry@university.com', 'Coventry University', 'coventry.com', 2);

INSERT INTO companies (created_at, email, name, website, address_id)
VALUES (now(), 'event.expensive@mail.com', 'Event Expensive', 'event.expensive.com', 5);

INSERT INTO companies (created_at, email, name, website, address_id)
VALUES (now(), 'mindthegap@group.com', 'Mind the Gap', 'mindthegap.com', 1);

INSERT INTO companies (created_at, email, name, website, address_id)
VALUES (now(), 'pyromusical@mail.com', 'Pyro Musical', 'pyromusical.com', 4);

-- 4 users -- all passwords are 'admin'
INSERT INTO users (created_at, email, encrypted_password, firstname, lastname, company_id, country_id)
VALUES (now(), 'admin@admin.com', '$2a$12$QcYlGG/vhXmwUb5nsf.gx.tcOC0DIk0R7P2NOjAwZH/aSZe1fuctq', 'Admin', 'Super', 3, 1);

INSERT INTO users (created_at, email, encrypted_password, firstname, lastname, company_id, country_id)
VALUES (now(), 'peter@mail.com', '$2a$12$QcYlGG/vhXmwUb5nsf.gx.tcOC0DIk0R7P2NOjAwZH/aSZe1fuctq', 'Peter', 'Pah', 2, 2);

INSERT INTO users (created_at, email, encrypted_password, firstname, lastname, company_id, country_id)
VALUES (now(), 'roger@worker.com', '$2a$12$QcYlGG/vhXmwUb5nsf.gx.tcOC0DIk0R7P2NOjAwZH/aSZe1fuctq', 'Roger', 'Santa', 2, 3);

INSERT INTO users (created_at, email, encrypted_password, firstname, lastname, country_id)
VALUES (now(), 'ze@customer.com', '$2a$12$QcYlGG/vhXmwUb5nsf.gx.tcOC0DIk0R7P2NOjAwZH/aSZe1fuctq', 'Ze', 'Padeiro', 1);

-- set addresses
INSERT INTO users_addresses (user_id, address_id) VALUES (1, 3);
INSERT INTO users_addresses (user_id, address_id) VALUES (2, 7);
INSERT INTO users_addresses (user_id, address_id) VALUES (3, 6);

-- set roles
INSERT INTO roles (user_id, roles) VALUES (1, 'ADMIN');
INSERT INTO roles (user_id, roles) VALUES (2, 'COMPANY_ADMIN');
INSERT INTO roles (user_id, roles) VALUES (3, 'WORKER');
INSERT INTO roles (user_id, roles) VALUES (4, 'CUSTOMER');

-- 1 event
INSERT INTO events (description, name, address_id, company_id, created_at)
VALUES ('Musical', 'El cantador', 8, 2, now());

-- 2 calendars
INSERT INTO calendars (end_date, start_date, company_id, event_id, created_at)
VALUES ('2023-04-10 21:00:00.000000', '2023-04-10 17:00:00.000000', 2, 1, now());

INSERT INTO calendars (end_date, start_date, company_id, event_id, created_at)
VALUES ('2023-04-12 19:00:00.000000', '2023-04-12 14:00:00.000000', 2, 1, now());

-- 2 Prices
INSERT INTO prices (price, type, company_id, event_id, created_at) VALUES (20, 'GENERAL', 2, 1, now());
INSERT INTO prices (price, type, company_id, event_id, created_at) VALUES (50, 'VIP', 2, 1, now());

-- 10 Tickets
INSERT INTO tickets (paypal_order_id, status, type, calendar_id, company_id, user_id, purchased_at)
VALUES ('uubawbdae9add-eae', 'SOLD', 'VIP', 1, 2, 4, now());

INSERT INTO tickets (paypal_order_id, status, type, calendar_id, company_id, user_id)
VALUES ('usdeeeadd-eae', 'WAITING_PAYMENT', 'GENERAL', 1, 2, 4);

INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 1, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 1, 2);

INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'GENERAL', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 2, 2);
INSERT INTO tickets (status, type, calendar_id, company_id) VALUES ('AVAILABLE', 'VIP', 2, 2);

