const cacheName = 'afterglow-static-v1'
const shellAssets = ['/', '/index.html', '/manifest.webmanifest', '/icon.svg']

self.addEventListener('install', (event) => {
  event.waitUntil(caches.open(cacheName).then((cache) => cache.addAll(shellAssets)))
})

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches
      .keys()
      .then((names) => Promise.all(names.filter((name) => name !== cacheName).map((name) => caches.delete(name)))),
  )
})

self.addEventListener('fetch', (event) => {
  const request = event.request

  if (request.method !== 'GET' || new URL(request.url).pathname.startsWith('/api')) {
    return
  }

  event.respondWith(caches.match(request).then((cached) => cached ?? fetch(request)))
})
