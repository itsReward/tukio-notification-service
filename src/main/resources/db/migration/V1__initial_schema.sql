-- Notification Templates table
CREATE TABLE IF NOT EXISTS notification_templates (
                                                      id BIGSERIAL PRIMARY KEY,
                                                      template_key VARCHAR(100) NOT NULL UNIQUE,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    description VARCHAR(255),
    channel VARCHAR(50) NOT NULL,
    template_type VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- User Notification Preferences table
CREATE TABLE IF NOT EXISTS user_notification_preferences (
                                                             id BIGSERIAL PRIMARY KEY,
                                                             user_id BIGINT NOT NULL,
                                                             notification_type VARCHAR(100) NOT NULL,
    email_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    push_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    in_app_enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, notification_type)
    );

-- Notifications table
CREATE TABLE IF NOT EXISTS notifications (
                                             id BIGSERIAL PRIMARY KEY,
                                             user_id BIGINT NOT NULL,
                                             title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    notification_type VARCHAR(100) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    reference_id VARCHAR(255),
    reference_type VARCHAR(100),
    sent_at TIMESTAMP,
    delivered_at TIMESTAMP,
    read_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Notification Delivery Attempts table
CREATE TABLE IF NOT EXISTS notification_delivery_attempts (
                                                              id BIGSERIAL PRIMARY KEY,
                                                              notification_id BIGINT NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    attempt_number INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
    );

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_channel ON notifications(channel);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(notification_type);
CREATE INDEX IF NOT EXISTS idx_notifications_reference ON notifications(reference_type, reference_id);
CREATE INDEX IF NOT EXISTS idx_notifications_created_at ON notifications(created_at);

CREATE INDEX IF NOT EXISTS idx_user_prefs_user_id ON user_notification_preferences(user_id);
CREATE INDEX IF NOT EXISTS idx_user_prefs_type ON user_notification_preferences(notification_type);

CREATE INDEX IF NOT EXISTS idx_templates_key ON notification_templates(template_key);
CREATE INDEX IF NOT EXISTS idx_templates_channel ON notification_templates(channel);

CREATE INDEX IF NOT EXISTS idx_delivery_attempts_notification_id ON notification_delivery_attempts(notification_id);
CREATE INDEX IF NOT EXISTS idx_delivery_attempts_status ON notification_delivery_attempts(status);
CREATE INDEX IF NOT EXISTS idx_delivery_attempts_attempted_at ON notification_delivery_attempts(attempted_at);

-- Insert default notification templates
INSERT INTO notification_templates (template_key, title, content, description, channel, template_type) VALUES
        ('EVENT_REGISTRATION_CONFIRMATION_EMAIL',
        'Registration Confirmed: {{eventName}}',
        '<div>Hello {{userName}},<br/><br/>Your registration for <strong>{{eventName}}</strong> has been confirmed.<br/><br/>Event Details:<br/>Date: {{eventDate}}<br/>Time: {{eventTime}}<br/>Location: {{eventLocation}}<br/><br/>We look forward to seeing you there!<br/><br/>Best regards,<br/>Tukio Campus Events Team</div>',
        'Email template sent to confirm event registration',
        'EMAIL',
        'HTML'),

        ('EVENT_REMINDER_EMAIL',
        'Reminder: {{eventName}} Tomorrow',
        '<div>Hello {{userName}},<br/><br/>This is a friendly reminder that <strong>{{eventName}}</strong> is scheduled for tomorrow.<br/><br/>Event Details:<br/>Date: {{eventDate}}<br/>Time: {{eventTime}}<br/>Location: {{eventLocation}}<br/><br/>We look forward to seeing you there!<br/><br/>Best regards,<br/>Tukio Campus Events Team</div>',
        'Email reminder sent before event starts',
        'EMAIL',
        'HTML'),

        ('EVENT_REGISTRATION_IN_APP',
        'Registration Confirmed',
        'Your registration for {{eventName}} has been confirmed.',
        'In-app notification for event registration',
        'IN_APP',
        'TEXT'),

        ('EVENT_REMINDER_PUSH',
        'Event Reminder',
        '{{eventName}} starts tomorrow at {{eventTime}}!',
        'Push notification reminder for upcoming event',
        'PUSH',
        'PUSH'),

        ('EVENT_CANCELLATION_EMAIL',
        'Event Cancelled: {{eventName}}',
        '<div>Hello {{userName}},<br/><br/>We regret to inform you that <strong>{{eventName}}</strong> scheduled for {{eventDate}} has been cancelled.<br/><br/>{{cancellationReason}}<br/><br/>We apologize for any inconvenience.<br/><br/>Best regards,<br/>Tukio Campus Events Team</div>',
        'Email notification for event cancellation',
        'EMAIL',
        'HTML'),

        ('EVENT_UPDATE_EMAIL',
        'Event Update: {{eventName}}',
        '<div>Hello {{userName}},<br/><br/>There has been an update to <strong>{{eventName}}</strong>.<br/><br/>{{updateDetails}}<br/><br/>Updated Event Details:<br/>Date: {{eventDate}}<br/>Time: {{eventTime}}<br/>Location: {{eventLocation}}<br/><br/>Best regards,<br/>Tukio Campus Events Team</div>',
        'Email notification for event updates',
        'EMAIL',
        'HTML'),

       ('VENUE_CHANGE_EMAIL',
        'Venue Change: {{eventName}}',
        '<div>Hello {{userName}},<br/><br/>The venue for <strong>{{eventName}}</strong> has been changed.<br/><br/>New Venue: {{newVenue}}<br/>Date: {{eventDate}}<br/>Time: {{eventTime}}<br/><br/>Please make note of this change.<br/><br/>Best regards,<br/>Tukio Campus Events Team</div>',
        'Email notification for venue changes',
        'EMAIL',
        'HTML'),

        ('SYSTEM_ANNOUNCEMENT_IN_APP',
        'System Announcement',
        '{{announcementText}}',
        'In-app notification for system announcements',
        'IN_APP',
        'TEXT')

    ON CONFLICT (template_key) DO NOTHING;