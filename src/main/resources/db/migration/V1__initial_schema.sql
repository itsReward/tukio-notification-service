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
    status VARCHAR(50) NOT NULL,
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
    notification_id BIGINT NOT NULL REFERENCES notifications(id),
    attempt_number INT NOT NULL,
    status VARCHAR(50) NOT NULL,
    error_message TEXT,
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better query performance
CREATE INDEX IF NOT EXISTS idx_notifications_user_id ON notifications(user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_status ON notifications(status);
CREATE INDEX IF NOT EXISTS idx_notifications_type ON notifications(notification_type);
CREATE INDEX IF NOT EXISTS idx_user_prefs_user_id ON user_notification_preferences(user_id);

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
 'TEXT')
ON CONFLICT (template_key) DO NOTHING;
