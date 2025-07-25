-- CSOFF: RegexpSinglelineCheck
-- CSOFF: LineLengthCheck
SELECT name, job_name
FROM users AS u
-- suppressed violation below (RegexpSinglelineCheck)
JOIN jobs AS j ON u.job_id = j.id
WHERE u.registration_date >= '2023-01-01' AND u.status = 'active';
-- // filtered violation above 'Line is longer ...'
