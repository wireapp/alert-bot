CREATE TABLE Alert (
 botId UUID NOT NULL PRIMARY KEY,
 conversationId UUID NOT NULL,
 serviceId UUID
);

CREATE TABLE Annotations (
 botId UUID NOT NULL,
 key varchar NOT NULL,
 value varchar NOT NULL,
 PRIMARY KEY(botId, key, value)
);

CREATE INDEX annotations_botId_idx ON Annotations (
	botId
);
