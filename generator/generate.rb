#!/usr/bin/env ruby

require 'elasticsearch'

index = 'demo'
type = 'event'
documents_count = 86400
client = Elasticsearch::Client.new log: true

if client.indices.exists index: index
  client.indices.delete index: index
end
client.indices.create index: index
client.indices.put_mapping index: index, type: type, body: {
  event: {
    properties: {
      start: { type: 'string', index: 'not_analyzed' },
      stop: { type: 'string', index: 'not_analyzed' }
    }
  }
}

body = []
(0...documents_count).each do |i|
  start = i
  stop = 2*i
  body << {
    index: {
      _index: index,
      _type: type,
      data: {
        start: sprintf('%02d:%02d:%02d', (start / 3600) % 24, (start / 60) % 60, start % 60),
        stop: sprintf('%02d:%02d:%02d', (stop / 3600) % 24, (stop / 60) % 60, stop % 60)
      }
    }
  }
end

client.bulk body: body
