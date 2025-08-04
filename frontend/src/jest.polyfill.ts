import { TextEncoder, TextDecoder } from 'util';

import { TransformStream } from 'web-streams-polyfill';

class DummyBroadcastChannel {
  constructor() {}
  postMessage() {}
  close() {}
  addEventListener() {}
  removeEventListener() {}
  onmessage = null;
}

globalThis.TextEncoder = TextEncoder;
globalThis.TextDecoder = TextDecoder;
globalThis.TransformStream = TransformStream;
globalThis.BroadcastChannel =
  DummyBroadcastChannel as unknown as typeof BroadcastChannel;
