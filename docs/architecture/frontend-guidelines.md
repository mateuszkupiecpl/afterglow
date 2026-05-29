# Frontend Guidelines

The frontend scaffold exists and is the first implementation surface for the
local one-device MVP.

## Stack

- React.
- TypeScript.
- Vite.
- PWA.
- Axios.
- Storybook.
- MSW.

## Tool Usage

- Use Storybook for isolated UI component development, visual states, and
  interaction examples.
- Use MSW to mock API flows, backend resources, and gameplay scenarios.
- Use Axios for HTTP calls once API-backed flows exist.
- Treat `/api` as the backend entry point when API-backed flows exist.
- Prefer backend-provided HATEOAS links for session actions over hardcoded
  frontend URL construction.
- Keep local fixtures or MSW handlers clearly separate from production API
  client code.

## Architecture

- Keep gameplay/domain logic testable outside React components.
- Keep UI components focused on rendering and interaction.
- Keep API resources separate from domain types, even in TypeScript.
- Design TypeScript domain types so they can later map cleanly to Java domain
  concepts.
- State should remain serializable where it represents a game session.

## MVP Guidance

- Prioritize the local one-device loop.
- Do not introduce backend dependencies unless explicitly requested.
- Do not implement public UGC, matchmaking, chat, payments, or native iOS.
- Do not introduce AI features unless explicitly requested.
- Card content must include metadata for type, spice level, target, and
  boundaries.
