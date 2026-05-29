export type ApiLink = {
  href: string
  templated?: boolean
  title?: string
  type?: string
}

export type ApiLinks = Record<string, ApiLink | ApiLink[] | undefined>

export type HalResource = {
  _links?: ApiLinks
}

export function firstLink(link: ApiLink | ApiLink[] | undefined): ApiLink | undefined {
  return Array.isArray(link) ? link[0] : link
}

export function linkHref(resource: HalResource, rel: string, variables?: Record<string, string>): string | undefined {
  const link = firstLink(resource._links?.[rel])

  if (!link) {
    return undefined
  }

  return expandTemplatedHref(link.href, variables)
}

export function requireLinkHref(resource: HalResource, rel: string, variables?: Record<string, string>): string {
  const href = linkHref(resource, rel, variables)

  if (!href) {
    throw new Error(`The '${rel}' action is not available for this session state.`)
  }

  return href
}

export function requestUrlFromHref(href: string): string {
  try {
    const parsed = new URL(href, window.location.origin)

    if (parsed.pathname.startsWith('/api')) {
      return `${parsed.pathname}${parsed.search}${parsed.hash}`
    }
  } catch {
    return href
  }

  return href
}

function expandTemplatedHref(href: string, variables: Record<string, string> = {}): string {
  return Object.entries(variables).reduce((expanded, [name, value]) => {
    const encodedValue = encodeURIComponent(value)
    return expanded.replaceAll(`{${name}}`, encodedValue).replaceAll(`%7B${name}%7D`, encodedValue)
  }, href)
}
