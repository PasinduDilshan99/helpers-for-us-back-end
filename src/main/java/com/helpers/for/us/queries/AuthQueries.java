package com.you.and.me.queries;

public class AuthQueries {
    public static final String GET_SECRET_QUESTIONS_AND_ANSWERS_BY_USERNAME = """
                SELECT 
                    sq.secret_question_id,
                    sq.question,
                    usq.secret_answer
                FROM user u
                INNER JOIN user_secret_question usq 
                    ON u.user_id = usq.user_id
                INNER JOIN secret_question sq 
                    ON sq.secret_question_id = usq.secret_question_id
                WHERE u.username = ?
                AND usq.status_id = (
                    SELECT id FROM common_status WHERE name = 'ACTIVE' LIMIT 1
                )
            """;

    public static final String RESET_PASSWORD_BY_USERNAME = """
                UPDATE user
                SET password = ?,
                    updated_at = CURRENT_TIMESTAMP
                WHERE username = ?

            """;
    public static final String CHANGE_PASSWORD_BY_USER_ID = """
    UPDATE user
    SET password = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE user_id = ?
""";

    // Insert
    public static final String INSERT_USER_SECRET_QUESTION = """
    INSERT INTO user_secret_question
    (
        user_id,
        secret_question_id,
        secret_answer,
        status_id,
        created_at
    )
    VALUES
    (
        ?,
        ?,
        ?,
        (SELECT id FROM common_status WHERE name = 'ACTIVE' LIMIT 1),
        CURRENT_TIMESTAMP
    )
""";

    // Update answer
    public static final String UPDATE_USER_SECRET_QUESTION = """
    UPDATE user_secret_question
    SET
        secret_answer = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE user_id = ?
    AND secret_question_id = ?
""";

    // Remove (soft delete recommended)
    public static final String REMOVE_USER_SECRET_QUESTION = """
    UPDATE user_secret_question
    SET
        status_id = (SELECT id FROM common_status WHERE name = 'INACTIVE' LIMIT 1),
        terminated_at = CURRENT_TIMESTAMP
    WHERE user_id = ?
    AND secret_question_id = ?
""";


    public static final String GET_PASSWORD_BY_USERNAME = """
    SELECT password
    FROM user
    WHERE username = ?
""";


}
