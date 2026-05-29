import { useState } from 'react'
import { useTranslation } from 'react-i18next'
import type { GameMode, PaceCode, SpiceLevel } from '../../game-session/model/gameSession'
import { modeOptions, paceOptions, spiceOptions } from '../../game-session/model/gameSession'
import { BoundaryPicker } from './BoundaryPicker'
import type { CreateSessionForm } from '../model/createSessionForm'
import * as React from "react";

type SetupViewProps = {
  busy: boolean
  onCreateSession: (form: CreateSessionForm) => void
}

export function SetupView({ busy, onCreateSession }: SetupViewProps) {
  const { t } = useTranslation()
  const [form, setForm] = useState<CreateSessionForm>({
    hostNickname: '',
    confirmedAdult: false,
    mode: 'party_warmup',
    startSpiceLevel: 'warmup',
    maxSpiceLevel: 'courage',
    pace: 'standard',
    allowProps: false,
    allowPairTasks: true,
    allowGroupTasks: true,
    boundaries: [],
  })

  function update<K extends keyof CreateSessionForm>(key: K, value: CreateSessionForm[K]) {
    setForm((current) => ({ ...current, [key]: value }))
  }

  function submit(event: React.SubmitEvent<HTMLFormElement>) {
    event.preventDefault()
    onCreateSession({ ...form, hostNickname: form.hostNickname.trim() })
  }

  const canSubmit = form.hostNickname.trim().length > 0 && form.confirmedAdult && !busy

  return (
    <section className="screen setup-screen" aria-labelledby="setup-title">
      <div className="screen-copy">
        <p className="eyebrow">{t('setup.createSession')}</p>
        <h2 id="setup-title">{t('setup.title')}</h2>
        <p className="lead-copy">{t('setup.lead')}</p>
      </div>

      <form className="flow-panel" onSubmit={submit}>
        <label className="field">
          <span>{t('setup.hostNickname')}</span>
          <input
            autoComplete="off"
            maxLength={40}
            required
            value={form.hostNickname}
            onChange={(event) => update('hostNickname', event.target.value)}
          />
        </label>

        <label className="confirm-row">
          <input
            required
            type="checkbox"
            checked={form.confirmedAdult}
            onChange={(event) => update('confirmedAdult', event.target.checked)}
          />
          <span>{t('setup.confirmAdult')}</span>
        </label>

        <div className="form-grid">
          <label className="field">
            <span>{t('setup.mode')}</span>
            <select value={form.mode} onChange={(event) => update('mode', event.target.value as GameMode)}>
              {modeOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {t(`game.mode.${option.value}.label`)}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>{t('setup.pace')}</span>
            <select value={form.pace} onChange={(event) => update('pace', event.target.value as PaceCode)}>
              {paceOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {t(`game.pace.${option.value}`)}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>{t('setup.startSpice')}</span>
            <select
              value={form.startSpiceLevel}
              onChange={(event) => update('startSpiceLevel', event.target.value as SpiceLevel)}
            >
              {spiceOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {t(`game.spice.${option.value}`)}
                </option>
              ))}
            </select>
          </label>

          <label className="field">
            <span>{t('setup.maxSpice')}</span>
            <select
              value={form.maxSpiceLevel}
              onChange={(event) => update('maxSpiceLevel', event.target.value as SpiceLevel)}
            >
              {spiceOptions.map((option) => (
                <option key={option.value} value={option.value}>
                  {t(`game.spice.${option.value}`)}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="toggle-row">
          <label>
            <input
              type="checkbox"
              checked={form.allowProps}
              onChange={(event) => update('allowProps', event.target.checked)}
            />
            <span>{t('setup.allowProps')}</span>
          </label>
          <label>
            <input
              type="checkbox"
              checked={form.allowPairTasks}
              onChange={(event) => update('allowPairTasks', event.target.checked)}
            />
            <span>{t('setup.allowPairTasks')}</span>
          </label>
          <label>
            <input
              type="checkbox"
              checked={form.allowGroupTasks}
              onChange={(event) => update('allowGroupTasks', event.target.checked)}
            />
            <span>{t('setup.allowGroupTasks')}</span>
          </label>
        </div>

        <BoundaryPicker selected={form.boundaries} onChange={(boundaries) => update('boundaries', boundaries)} />

        <button className="primary-action" disabled={!canSubmit} type="submit">
          {t('setup.createSession')}
        </button>
      </form>
    </section>
  )
}
